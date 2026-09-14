package com.honeybudget.domain;

import com.honeybudget.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.Objects;

/**
 * [자산(계좌) 마스터 엔티티]
 * 계좌를 정의하는 정적 데이터.
 * 예: 생활비 통장, 고정비 통장, 자동차 통장, 주식 계좌 등
 */
@Entity
@Table(
    name = "assets",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_asset_name", columnNames = "name")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Asset extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private AssetRole role;

    @Builder
    private Asset(String name, AssetRole role) {
        this.name = validateName(name);
        this.role = Objects.requireNonNull(role, "자산 역할은 필수입니다.");
    }

    public static Asset create(String name, AssetRole role) {
        return Asset.builder()
                .name(name)
                .role(role)
                .build();
    }

    /**
     * 비즈니스 메서드: 자산 정보 수정
     */
    public void update(String name, AssetRole role) {
        this.name = validateName(name);
        this.role = Objects.requireNonNull(role, "자산 역할은 필수입니다.");
    }

    /**
     * 비즈니스 메서드: 자산 이름 수정
     */
    public void updateName(String name) {
        this.name = validateName(name);
    }

    /**
     * 비즈니스 메서드: 자산 역할 수정
     */
    public void updateRole(AssetRole role) {
        this.role = Objects.requireNonNull(role, "자산 역할은 필수입니다.");
    }

    /**
     * 비즈니스 편의 메서드: 지출 목적 자산인지 확인
     */
    public boolean isExpenseAsset() {
        return this.role == AssetRole.EXPENSE;
    }

    /**
     * 비즈니스 편의 메서드: 투자 목적 자산인지 확인
     */
    public boolean isInvestmentAsset() {
        return this.role == AssetRole.INVESTMENT;
    }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "자산 이름은 필수입니다.");
        if (name.isBlank()) {
            throw new IllegalArgumentException("자산 이름은 공백일 수 없습니다.");
        }
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Asset asset = (Asset) o;
        return getId() != null && Objects.equals(getId(), asset.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
