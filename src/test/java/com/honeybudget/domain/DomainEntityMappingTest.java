package com.honeybudget.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DomainEntityMappingTest {

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("Asset 생성 및 Auditing 확인")
    void testAssetPersistence() {
        Asset asset = Asset.create("생활비통장", AssetRole.EXPENSE);
        em.persist(asset);
        em.flush();
        em.clear();

        Asset found = em.find(Asset.class, asset.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("생활비통장");
        assertThat(found.getRole()).isEqualTo(AssetRole.EXPENSE);
        assertThat(found.getCreatedAt()).isNotNull();
        assertThat(found.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Category 생성 및 수정 확인")
    void testCategoryPersistence() {
        Category category = Category.create("식비");
        em.persist(category);
        em.flush();
        em.clear();

        Category found = em.find(Category.class, category.getId());
        assertThat(found.getName()).isEqualTo("식비");

        found.updateName("외식비");
        em.flush();
        em.clear();

        Category updated = em.find(Category.class, category.getId());
        assertThat(updated.getName()).isEqualTo("외식비");
    }

    @Test
    @DisplayName("MonthlyBudget 계산 로직 및 YearMonth 컨버터 확인")
    void testMonthlyBudget() {
        YearMonth month = YearMonth.of(2026, 9);
        MonthlyBudget budget = MonthlyBudget.create(
                month,
                new BigDecimal("5000000.00"),
                new BigDecimal("1500000.00"),
                new BigDecimal("1200000.00")
        );

        assertThat(budget.calculateTotalAllocated()).isEqualByComparingTo("2700000.00");
        assertThat(budget.calculateSurplus()).isEqualByComparingTo("2300000.00");

        em.persist(budget);
        em.flush();
        em.clear();

        MonthlyBudget found = em.find(MonthlyBudget.class, budget.getId());
        assertThat(found.getRecordMonth()).isEqualTo(month);
        assertThat(found.getTotalIncome()).isEqualByComparingTo("5000000.00");
    }

    @Test
    @DisplayName("AssetSnapshot 연관관계 및 잔액 수정 확인")
    void testAssetSnapshot() {
        Asset asset = Asset.create("주식계좌", AssetRole.INVESTMENT);
        em.persist(asset);

        YearMonth month = YearMonth.of(2026, 9);
        AssetSnapshot snapshot = AssetSnapshot.create(asset, month, new BigDecimal("10000000.00"));
        em.persist(snapshot);
        em.flush();
        em.clear();

        AssetSnapshot found = em.find(AssetSnapshot.class, snapshot.getId());
        assertThat(found.getAsset().getId()).isEqualTo(asset.getId());
        assertThat(found.getRecordMonth()).isEqualTo(month);
        assertThat(found.getBalance()).isEqualByComparingTo("10000000.00");
    }

    @Test
    @DisplayName("Transaction Aggregate 영속성 전이(Cascade) 및 고아 객체(OrphanRemoval) 제거 확인")
    void testTransactionAggregateCascadeAndOrphanRemoval() {
        Asset asset = Asset.create("체크카드통장", AssetRole.EXPENSE);
        Category category = Category.create("식료품");
        em.persist(asset);
        em.persist(category);

        Transaction transaction = Transaction.create(
                LocalDate.of(2026, 9, 14),
                asset,
                category,
                "이마트",
                new BigDecimal("45000.00")
        );

        TransactionDetail detail1 = TransactionDetail.create("닭안심", new BigDecimal("15000.00"));
        TransactionDetail detail2 = TransactionDetail.create("양배추", new BigDecimal("5000.00"));

        transaction.addDetail(detail1);
        transaction.addDetail(detail2);

        assertThat(transaction.calculateDetailSum()).isEqualByComparingTo("20000.00");

        // Transaction만 persist해도 details가 함께 저장되는지 확인
        em.persist(transaction);
        em.flush();
        em.clear();

        Transaction found = em.find(Transaction.class, transaction.getId());
        assertThat(found.getDetails()).hasSize(2);
        assertThat(found.getDetails().get(0).getItemName()).isIn("닭안심", "양배추");

        // 방어적 복사 확인: getDetails() 직접 변경 시도 시 예외 발생
        assertThatThrownBy(() -> found.getDetails().clear())
                .isInstanceOf(UnsupportedOperationException.class);

        // 고아 객체 제거 테스트
        TransactionDetail target = found.getDetails().get(0);
        found.removeDetail(target);
        em.flush();
        em.clear();

        Transaction afterRemoval = em.find(Transaction.class, transaction.getId());
        assertThat(afterRemoval.getDetails()).hasSize(1);
    }
}
