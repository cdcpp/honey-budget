package com.honeybudget.domain;

import com.honeybudget.domain.Category;
import com.honeybudget.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 계좌 엔티티. 계좌 기본 정보와 소속 카테고리를 관리한다.
 */
@Entity
@Table(name = "account")
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

    /** 대쉬보드 잔액관리 계좌 출력여부(true/false)*/
    @Column(name="is_dashboard_visible" , nullable = false ,  columnDefinition = "boolean default false")
    private Boolean isDashboardVisible = false;

    /** 월별 소비내역 소비계좌 출력여부(true/false)*/
    @Column(name="is_spending_visible" , nullable = false ,  columnDefinition = "boolean default false")
    private Boolean isSpendingVisible = false;

    /** 계좌 현재 잔액 */
    @Column(name = "balance", nullable = false, precision = 15, scale = 0, columnDefinition = "decimal(15,0) default 0")
    private BigDecimal balance = BigDecimal.ZERO;

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
            Category category,
            String memo,
            Boolean isDashboardVisible,
            Boolean isSpendingVisible,
            BigDecimal balance,
            Boolean useYn
    ) {
        this.accountName = accountName;
        this.institutionName = institutionName;
        this.category = category;
        this.memo = memo;
        this.useYn = useYn != null ? useYn : true; // 기본 활성화 상태
        this.isDashboardVisible = Boolean.TRUE.equals(isDashboardVisible);
        this.isSpendingVisible = Boolean.TRUE.equals(isSpendingVisible);
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    // ================= 비즈니스 메서드 (도메인 로직) ================= //

    /**
     * 계좌 정보(명칭, 금융기관, 카테고리, 메모, 노출여부, 잔액)를 통합 수정한다.
     */
    public void update(
            String accountName,
            String institutionName,
            Category category,
            String memo,
            Boolean isDashboardVisible,
            Boolean isSpendingVisible,
            BigDecimal balance
    ) {
        this.accountName = accountName;
        if (institutionName != null && !institutionName.isBlank()) {
            this.institutionName = institutionName;
        }
        if (category != null) {
            this.category = category;
        }
        this.memo = memo;
        if (isDashboardVisible != null) {
            this.isDashboardVisible = isDashboardVisible;
        }
        if (isSpendingVisible != null) {
            this.isSpendingVisible = isSpendingVisible;
        }
        if (balance != null) {
            this.balance = balance;
        }
    }

}
