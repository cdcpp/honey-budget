package com.honeybudget.domain;

import com.honeybudget.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;

/**
 * [월간 수입 및 예산 분배 현황판 엔티티]
 * 매월 1회 총 수입(급여 등)을 기록하고,
 * 생활비, 고정비 등 목적별 통장으로 예산을 얼마씩 쪼개어 할당했는지 관리.
 */
@Entity
@Table(
    name = "monthly_budgets",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_monthly_budget_month", columnNames = "record_month")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyBudget extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    /** 기준월 (예: "2026-09", YearMonthAttributeConverter 적용) */
    @Column(name = "record_month", nullable = false, length = 7)
    private YearMonth recordMonth;

    /** 해당 월의 총 수입 (급여, 상여금 등) */
    @Column(name = "total_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalIncome;

    /** 식비/비고정지출 등을 위해 생활비 통장으로 할당한 금액 */
    @Column(name = "living_expense", nullable = false, precision = 15, scale = 2)
    private BigDecimal livingExpense;

    /** 세금/통신비 등을 위해 고정비 통장으로 할당한 금액 */
    @Column(name = "fixed_expense", nullable = false, precision = 15, scale = 2)
    private BigDecimal fixedExpense;

    @Builder
    private MonthlyBudget(YearMonth recordMonth, BigDecimal totalIncome, BigDecimal livingExpense, BigDecimal fixedExpense) {
        this.recordMonth = Objects.requireNonNull(recordMonth, "기준월은 필수입니다.");
        this.totalIncome = validateNonNegative(totalIncome, "총 수입");
        this.livingExpense = validateNonNegative(livingExpense, "생활비 할당액");
        this.fixedExpense = validateNonNegative(fixedExpense, "고정비 할당액");
    }

    public static MonthlyBudget create(YearMonth recordMonth, BigDecimal totalIncome, BigDecimal livingExpense, BigDecimal fixedExpense) {
        return MonthlyBudget.builder()
                .recordMonth(recordMonth)
                .totalIncome(totalIncome)
                .livingExpense(livingExpense)
                .fixedExpense(fixedExpense)
                .build();
    }

    /**
     * 비즈니스 메서드: 예산 수정
     */
    public void updateBudget(BigDecimal totalIncome, BigDecimal livingExpense, BigDecimal fixedExpense) {
        this.totalIncome = validateNonNegative(totalIncome, "총 수입");
        this.livingExpense = validateNonNegative(livingExpense, "생활비 할당액");
        this.fixedExpense = validateNonNegative(fixedExpense, "고정비 할당액");
    }

    /**
     * 비즈니스 도메인 로직: 총 할당 예산 합산 (생활비 + 고정비)
     */
    public BigDecimal calculateTotalAllocated() {
        return this.livingExpense.add(this.fixedExpense);
    }

    /**
     * 비즈니스 도메인 로직: 잔여/여유 자금 (총 수입 - 총 할당 예산)
     */
    public BigDecimal calculateSurplus() {
        return this.totalIncome.subtract(calculateTotalAllocated());
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
        MonthlyBudget that = (MonthlyBudget) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
