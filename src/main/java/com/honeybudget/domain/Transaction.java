package com.honeybudget.domain;

import com.honeybudget.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * [결제/지출 내역 Aggregate Root 엔티티]
 * 일상적인 결제(영수증 1건) 단위의 데이터를 기록.
 * "어느 통장(Asset)에서, 어떤 목적(Category)으로 얼마가 나갔다"를 추적.
 * 영수증 세부 품목(TransactionDetail)의 라이프사이클을 통제하는 애그리거트 루트입니다.
 */
@Entity
@Table(
    name = "transactions",
    indexes = {
        @Index(name = "idx_transaction_pay_date", columnList = "pay_date"),
        @Index(name = "idx_transaction_asset_date", columnList = "asset_id, pay_date"),
        @Index(name = "idx_transaction_category_date", columnList = "category_id, pay_date")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    /** 실제 결제가 이루어진 일자 */
    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;

    /** 돈이 빠져나간 출처 통장 (단방향 N:1 연관관계) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_asset"))
    private Asset asset;

    /** 지출 분류 (단방향 N:1 연관관계) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_category"))
    private Category category;

    /** 결제처 및 상호명 (예: 쿠팡, 주유소, 병원) */
    @Column(name = "merchant", nullable = false, length = 100)
    private String merchant;

    /** 해당 결제건의 총 지출 금액 */
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /** 영수증 내 세부 구매 품목 리스트 (부모 애그리거트 루트와 생명주기를 같이 함) */
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionDetail> details = new ArrayList<>();

    @Builder
    private Transaction(LocalDate payDate, Asset asset, Category category, String merchant, BigDecimal amount) {
        this.payDate = Objects.requireNonNull(payDate, "결제일자는 필수입니다.");
        this.asset = Objects.requireNonNull(asset, "자산은 필수입니다.");
        this.category = Objects.requireNonNull(category, "카테고리는 필수입니다.");
        this.merchant = Objects.requireNonNull(merchant, "가맹점/상호명은 필수입니다.");
        this.amount = validateNonNegative(amount, "결제 금액");
    }

    public static Transaction create(LocalDate payDate, Asset asset, Category category, String merchant, BigDecimal amount) {
        return Transaction.builder()
                .payDate(payDate)
                .asset(asset)
                .category(category)
                .merchant(merchant)
                .amount(amount)
                .build();
    }

    /**
     * 외부에서 세부 품목 컬렉션을 직접 변경(add/remove/clear)하지 못하도록 방어적 불변 뷰를 제공합니다.
     */
    public List<TransactionDetail> getDetails() {
        return Collections.unmodifiableList(details);
    }

    /**
     * 영수증 세부 품목 추가 (양방향 연관관계 편의 메서드)
     */
    public void addDetail(TransactionDetail detail) {
        Objects.requireNonNull(detail, "세부 품목은 null일 수 없습니다.");
        this.details.add(detail);
        detail.assignTransaction(this);
    }

    /**
     * 영수증 세부 품목 일괄 추가
     */
    public void addDetails(List<TransactionDetail> details) {
        if (details != null) {
            details.forEach(this::addDetail);
        }
    }

    /**
     * 영수증 세부 품목 제거 (orphanRemoval 작동)
     */
    public void removeDetail(TransactionDetail detail) {
        if (detail != null && this.details.remove(detail)) {
            detail.assignTransaction(null);
        }
    }

    /**
     * 등록된 세부 품목들의 금액 합산 계산
     */
    public BigDecimal calculateDetailSum() {
        return details.stream()
                .map(TransactionDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 비즈니스 메서드: 결제 정보 수정
     */
    public void update(LocalDate payDate, Asset asset, Category category, String merchant, BigDecimal amount) {
        this.payDate = Objects.requireNonNull(payDate, "결제일자는 필수입니다.");
        this.asset = Objects.requireNonNull(asset, "자산은 필수입니다.");
        this.category = Objects.requireNonNull(category, "카테고리는 필수입니다.");
        this.merchant = Objects.requireNonNull(merchant, "가맹점/상호명은 필수입니다.");
        this.amount = validateNonNegative(amount, "결제 금액");
    }

    private static BigDecimal validateNonNegative(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + "은(는) 필수입니다.");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + "은(는) 0 이상이어야 합니다.");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Transaction that = (Transaction) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
