package com.honeybudget.domain;

import com.honeybudget.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * 계좌 거래 내역 엔티티. 일자별 수입 및 지출 상세 내역을 관리한다.
 */
@Entity
@Table(
        name = "account_transaction",
        indexes = {
                @Index(name = "idx_account_trans_ym", columnList = "account_id, year_month"),
                @Index(name = "idx_account_trans_date", columnList = "account_id, transaction_date")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountTransaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_transaction_id")
    private Long id;

    /** 대상 계좌 (FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "account_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_transaction_to_account")
    )
    private Account account;

    /**
     * 월별 시트 기준 연월 (예: 2025-01, 2025-02)
     * YearMonthAttributeConverter를 통해 "YYYY-MM" 형식으로 저장/조회됩니다.
     */
    @Column(name = "year_month", nullable = false, length = 7)
    private YearMonth yearMonth;

    /** 거래 일자 (예: 2025-01-15) */
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    /** 지출/거래 금액 */
    @Column(name = "amount", nullable = false, precision = 15, scale = 0)
    private BigDecimal amount;

    /** 사용처 / 내용 (예: "이마트 장보기", "GS칼텍스 주유", "정형외과 진료") */
    @Column(name = "description", nullable = false, length = 100)
    private String description;

    /** 비고 / 메모 */
    @Column(name = "memo", length = 255)
    private String memo;

    @Builder
    public AccountTransaction(
            Account account,
            LocalDate transactionDate,
            YearMonth yearMonth,
            BigDecimal amount,
            String description,
            String memo
    ) {
        if (account == null) {
            throw new IllegalArgumentException("계좌 정보는 필수입니다.");
        }
        if (transactionDate == null) {
            throw new IllegalArgumentException("거래 일자는 필수입니다.");
        }
        if (amount == null) {
            throw new IllegalArgumentException("거래 금액은 필수입니다.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("사용처/내용은 필수입니다.");
        }

        this.account = account;
        this.transactionDate = transactionDate;
        // yearMonth가 별도로 전달되지 않은 경우 transactionDate로부터 자동 추출
        this.yearMonth = (yearMonth != null) ? yearMonth : YearMonth.from(transactionDate);
        this.amount = amount;
        this.description = description.trim();
        this.memo = memo != null ? memo.trim() : null;
    }

    // ================= 비즈니스 메서드 (도메인 로직) ================= //

    /**
     * 거래 정보를 수정한다. 일자 변경 시 귀속 연월(yearMonth)도 자동 갱신된다.
     */
    public void updateTransaction(
            LocalDate transactionDate,
            BigDecimal amount,
            String description,
            String memo
    ) {
        if (transactionDate != null) {
            this.transactionDate = transactionDate;
            this.yearMonth = YearMonth.from(transactionDate);
        }
        if (amount != null) {
            this.amount = amount;
        }
        if (description != null && !description.isBlank()) {
            this.description = description.trim();
        }
        this.memo = memo != null ? memo.trim() : null;
    }
}
