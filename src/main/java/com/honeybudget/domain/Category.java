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
 * [지출 카테고리 기준 엔티티]
 * 가계부 내역의 성격을 분류하기 위한 메타데이터.
 * 예: 식비, 고정비용, 비고정지출, 자동차/병원비 등
 */
@Entity
@Table(
    name = "categories",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_category_name", columnNames = "name")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    /** 분류/카테고리명 (예: "식비", "자동차/병원비") */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Builder
    private Category(String name) {
        this.name = Objects.requireNonNull(name, "카테고리 이름은 필수입니다.");
    }

    public static Category create(String name) {
        return Category.builder()
                .name(name)
                .build();
    }

    /** 비즈니스 메서드: 카테고리명 변경 */
    public void updateName(String name) {
        this.name = Objects.requireNonNull(name, "카테고리 이름은 필수입니다.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Category category = (Category) o;
        return getId() != null && Objects.equals(getId(), category.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
