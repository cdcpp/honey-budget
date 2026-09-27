package com.honeybudget.domain;

import com.honeybudget.domain.Category;
import com.honeybudget.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_institution_number",
                        columnNames = {"institution_name", "account_number"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    /** 계좌 별칭/명칭 (예: "토스 생활비 통장", "KB 비상금/병원", "한투 주식계좌") */
    @Column(name = "account_name", nullable = false, length = 50)
    private String accountName;

    /** 금융기관/은행/증권사명 (예: "토스뱅크", "국민은행", "한국투자증권") */
    @Column(name = "institution_name", nullable = false, length = 30)
    private String institutionName;

    /** 계좌번호 (하이픈 포함 가능, 최대 50자) */
    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    /** 현재 잔액 / 예수금 */
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    /**
     * 계좌 목적 카테고리 (중분류 Category 참조)
     * 예: "101"(생활비), "201"(비상금), "301"(주식투자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_to_category")
    )
    private Category category;

    /** 계좌 활성 여부 (통장 해지/삭제 시 false 처리 - Soft Delete) */
    @Column(name = "use_yn", nullable = false, length = 1)
    private Boolean useYn;

    /** 비고/메모 */
    @Column(name = "memo", length = 200)
    private String memo;

    @Builder
    public Account(
            String accountName,
            String institutionName,
            String accountNumber,
            BigDecimal balance,
            Category category,
            String memo
    ) {
        this.accountName = accountName;
        this.institutionName = institutionName;
        this.accountNumber = accountNumber;
        this.balance = (balance != null) ? balance : BigDecimal.ZERO;
        this.category = category;
        this.memo = memo;
        this.useYn = true; // 기본 활성화 상태
    }

    // ================= 비즈니스 메서드 (도메인 로직) ================= //

    /**
     * 계좌 기본 정보 수정
     */
    public void updateAccountInfo(String accountName, String memo) {
        this.accountName = accountName;
        this.memo = memo;
    }

    /**
     * 계좌 목적 카테고리 변경 (통장 용도 전환 시)
     */
    public void changeCategory(Category newCategory) {
        if (newCategory == null) {
            throw new IllegalArgumentException("변경할 목적 카테고리는 필수입니다.");
        }
        this.category = newCategory;
    }

    /**
     * 잔액 입금 / 증가
     */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("입금 금액은 0보다 커야 합니다.");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * 잔액 출금 / 차감 (마이너스 통장이 아닐 경우 음수 방지)
     */
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("출금 금액은 0보다 커야 합니다.");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다. (현재 잔액: " + this.balance + ")");
        }
        this.balance = this.balance.subtract(amount);
    }

    /**
     * 계좌 비활성화 / 해지 처리 (Soft Delete)
     */
    public void deactivate() {
        this.useYn = false;
    }
}
