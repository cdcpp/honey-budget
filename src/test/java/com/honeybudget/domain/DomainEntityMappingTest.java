package com.honeybudget.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DomainEntityMappingTest {

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("CategoryGroup 및 Category 연관관계 영속화 및 Auditing 확인")
    void testCategoryGroupAndCategoryPersistence() {
        // 대분류 생성
        CategoryGroup group = CategoryGroup.builder()
                .groupCode("100")
                .groupName("수입")
                .build();
        em.persist(group);

        // 중분류 생성
        Category category = Category.builder()
                .group(group)
                .categoryCode("101")
                .categoryName("급여")
                .build();
        em.persist(category);

        em.flush();
        em.clear();

        // 조회 검증
        Category foundCategory = em.find(Category.class, category.getId());
        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getCategoryCode()).isEqualTo("101");
        assertThat(foundCategory.getCategoryName()).isEqualTo("급여");
        assertThat(foundCategory.getGroup()).isNotNull();
        assertThat(foundCategory.getGroup().getCategoryGroupCode()).isEqualTo("100");
        assertThat(foundCategory.getGroup().getCategoryGroupName()).isEqualTo("수입");
        assertThat(foundCategory.getCreatedAt()).isNotNull();
        assertThat(foundCategory.getUpdatedAt()).isNotNull();
    }
}
