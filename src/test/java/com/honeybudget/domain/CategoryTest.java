package com.honeybudget.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    @DisplayName("CategoryGroup 엔티티 생성 및 명칭 수정 검증")
    void testCategoryGroup() {
        CategoryGroup group = CategoryGroup.builder()
                .groupCode("100")
                .groupName("수입")
                .build();

        assertThat(group.getCategoryGroupCode()).isEqualTo("100");
        assertThat(group.getCategoryGroupName()).isEqualTo("수입");

        group.updateGroupName("수입 (급여/기타)");
        assertThat(group.getCategoryGroupName()).isEqualTo("수입 (급여/기타)");
    }

    @Test
    @DisplayName("Category 엔티티 생성 검증")
    void testCategory() {
        CategoryGroup group = CategoryGroup.builder()
                .groupCode("200")
                .groupName("지출")
                .build();

        Category category = Category.builder()
                .group(group)
                .categoryCode("201")
                .categoryName("생활비")
                .build();

        assertThat(category.getCategoryCode()).isEqualTo("201");
        assertThat(category.getCategoryName()).isEqualTo("생활비");
        assertThat(category.getGroup()).isEqualTo(group);
    }
}
