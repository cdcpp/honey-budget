package com.honeybudget.domain;

import com.honeybudget.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * [영수증 세부 품목 엔티티]
 * 하나의 결제 건(Transaction)에 포함된 개별 품목.
 * Transaction 애그리거트 루트의 경계 내부에서 관리됩니다.
 */
@Entity
@Table(
    name = "transaction_details",
    indexes = {
        @Index(name = "idx_detail_transaction_id", columnList = "transaction_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long id;

    /** 소속된 부모 결제 내역 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, foreignKey = @ForeignKey(name = "fk_detail_transaction"))
    private Transaction transaction;

    /** 개별 구매 품목명 (예: "닭안심", "양배추") */
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    /** 개별 품목의 가격 */
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Builder
    private TransactionDetail(String itemName, BigDecimal amount) {
        this.itemName = Objects.requireNonNull(itemName, "품목 이름은 필수입니다.");
        this.amount = validateNonNegative(amount, "품목 금액");
    }

    public static TransactionDetail create(String itemName, BigDecimal amount) {
        return TransactionDetail.builder()
                .itemName(itemName)
                .amount(amount)
                .build();
    }

    /**
     * 부모 Transaction 설정 (연관관계 편의 메서드용 - 외부 패키지 임의 호출 방지를 위해 package-private)
     */
    void assignTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * 비즈니스 메서드: 품목 정보 수정
     */
    public void update(String itemName, BigDecimal amount) {
        this.itemName = Objects.requireNonNull(itemName, "품목 이름은 필수입니다.");
        this.amount = validateNonNegative(amount, "품목 금액");
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
        TransactionDetail that = (TransactionDetail) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
