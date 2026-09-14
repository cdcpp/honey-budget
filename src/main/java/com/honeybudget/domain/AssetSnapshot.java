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
 * [월별 자산 잔액 이력 엔티티]
 * 매월 1회 각 자산(통장/주식 등)의 마감 잔액을 기록.
 * 과거 특정 월의 자산 현황과 자산의 증감 추이(차트) 조회에 활용.
 */
@Entity
@Table(
    name = "asset_snapshots",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_asset_snapshot_asset_month",
            columnNames = {"asset_id", "record_month"}
        )
    },
    indexes = {
        @Index(name = "idx_asset_snapshot_month", columnList = "record_month")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetSnapshot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snapshot_id")
    private Long id;

    /** 대상 자산(통장) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(name = "fk_snapshot_asset"))
    private Asset asset;

    /** 기준월 (YearMonthAttributeConverter에 의해 VARCHAR(7) 'YYYY-MM' 형식으로 변환) */
    @Column(name = "record_month", nullable = false, length = 7)
    private YearMonth recordMonth;

    /** 해당 기준월에 확인된 최종 잔액 또는 주식 평가금액 */
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Builder
    private AssetSnapshot(Asset asset, YearMonth recordMonth, BigDecimal balance) {
        this.asset = Objects.requireNonNull(asset, "자산은 필수입니다.");
        this.recordMonth = Objects.requireNonNull(recordMonth, "기록 기준월은 필수입니다.");
        this.balance = Objects.requireNonNull(balance, "잔액은 필수입니다.");
    }

    public static AssetSnapshot create(Asset asset, YearMonth recordMonth, BigDecimal balance) {
        return AssetSnapshot.builder()
                .asset(asset)
                .recordMonth(recordMonth)
                .balance(balance)
                .build();
    }

    /** 비즈니스 메서드: 잔액 갱신 */
    public void updateBalance(BigDecimal newBalance) {
        this.balance = Objects.requireNonNull(newBalance, "잔액은 필수입니다.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AssetSnapshot that = (AssetSnapshot) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
