package com.honeybudget.domain;

import com.honeybudget.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "category_group",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_group_code", columnNames = {"category_group_code"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryGroup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_group_id")
    private Long id;

    /** 대분류 코드 (예: "100", "200") */
    @Column(name = "category_group_code", nullable = false, length = 10)
    private String categoryGroupCode;

    /** 대분류 명칭 (예: "수입", "지출") */
    @Column(name = "category_group_name", nullable = false, length = 20)
    private String categoryGroupName;

    @Builder
    public CategoryGroup(String groupCode, String groupName) {
        this.categoryGroupCode = groupCode;
        this.categoryGroupName = groupName;
    }

    public void updateGroupName(String groupName) {
        this.categoryGroupName = groupName;
    }
}
