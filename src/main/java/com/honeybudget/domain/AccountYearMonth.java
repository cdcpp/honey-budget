package com.honeybudget.domain;

import com.honeybudget.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 계좌별 연간 월별(1~12월) 금액 관리 엔티티. 계좌와 연도 조합당 1건만 존재한다.
 */
@Entity
@Table(
        name = "accountyearmonth",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_account_year",
                columnNames = {"account_id", "base_year"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountYearMonth extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_yearmonth_id")
    private Long id;

    /** 계좌 (외래키) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "account_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_yearmonth_to_account")
    )
    private Account account;

    /** 연도 (예: 2026) - H2 및 SQL 표준 예약어 충돌 방지를 위해 컬럼명을 base_year로 매핑 */
    @Column(name = "base_year", nullable = false)
    private Integer year;

    @Column(name = "month_01", nullable = false, precision = 15, scale = 0)
    private BigDecimal month01 = BigDecimal.ZERO;

    @Column(name = "month_02", nullable = false, precision = 15, scale = 0)
    private BigDecimal month02 = BigDecimal.ZERO;

    @Column(name = "month_03", nullable = false, precision = 15, scale = 0)
    private BigDecimal month03 = BigDecimal.ZERO;

    @Column(name = "month_04", nullable = false, precision = 15, scale = 0)
    private BigDecimal month04 = BigDecimal.ZERO;

    @Column(name = "month_05", nullable = false, precision = 15, scale = 0)
    private BigDecimal month05 = BigDecimal.ZERO;

    @Column(name = "month_06", nullable = false, precision = 15, scale = 0)
    private BigDecimal month06 = BigDecimal.ZERO;

    @Column(name = "month_07", nullable = false, precision = 15, scale = 0)
    private BigDecimal month07 = BigDecimal.ZERO;

    @Column(name = "month_08", nullable = false, precision = 15, scale = 0)
    private BigDecimal month08 = BigDecimal.ZERO;

    @Column(name = "month_09", nullable = false, precision = 15, scale = 0)
    private BigDecimal month09 = BigDecimal.ZERO;

    @Column(name = "month_10", nullable = false, precision = 15, scale = 0)
    private BigDecimal month10 = BigDecimal.ZERO;

    @Column(name = "month_11", nullable = false, precision = 15, scale = 0)
    private BigDecimal month11 = BigDecimal.ZERO;

    @Column(name = "month_12", nullable = false, precision = 15, scale = 0)
    private BigDecimal month12 = BigDecimal.ZERO;

    @Builder
    public AccountYearMonth(Account account, Integer year) {
        this.account = account;
        this.year = year;
    }

    // ================= 비즈니스 메서드 ================= //

    /**
     * 특정 월(1~12)의 금액을 수정한다.
     */
    public void updateAmount(int month, BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("금액은 필수입니다.");
        }
        switch (month) {
            case 1 -> this.month01 = amount;
            case 2 -> this.month02 = amount;
            case 3 -> this.month03 = amount;
            case 4 -> this.month04 = amount;
            case 5 -> this.month05 = amount;
            case 6 -> this.month06 = amount;
            case 7 -> this.month07 = amount;
            case 8 -> this.month08 = amount;
            case 9 -> this.month09 = amount;
            case 10 -> this.month10 = amount;
            case 11 -> this.month11 = amount;
            case 12 -> this.month12 = amount;
            default -> throw new IllegalArgumentException("월은 1~12 사이여야 합니다: " + month);
        }
    }

    /**
     * 특정 월(1~12)의 금액을 조회한다.
     */
    public BigDecimal getAmount(int month) {
        return switch (month) {
            case 1 -> month01;
            case 2 -> month02;
            case 3 -> month03;
            case 4 -> month04;
            case 5 -> month05;
            case 6 -> month06;
            case 7 -> month07;
            case 8 -> month08;
            case 9 -> month09;
            case 10 -> month10;
            case 11 -> month11;
            case 12 -> month12;
            default -> throw new IllegalArgumentException("월은 1~12 사이여야 합니다: " + month);
        };
    }
}
