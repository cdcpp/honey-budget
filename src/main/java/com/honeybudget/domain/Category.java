package com.honeybudget.domain;

import com.honeybudget.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_code", columnNames = {"category_code"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    /** 속한 대분류 그룹 (FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_group_id", nullable = false, foreignKey = @ForeignKey(name = "fk_category_to_group"))
    private CategoryGroup group;

    /** 중분류 코드 (예: "101", "201") */
    @Column(name = "category_code", nullable = false, length = 10)
    private String categoryCode;

    /** 중분류 명칭 (예: "생활비 통장", "자동차/병원비통장") */
    @Column(name = "category_name", nullable = false, length = 20)
    private String categoryName;

    @Builder
    public Category(CategoryGroup group, String categoryCode, String categoryName) {
        this.group = group;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }
}