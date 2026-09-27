package com.honeybudget.service;

import com.honeybudget.domain.Category;
import com.honeybudget.domain.CategoryGroup;
import com.honeybudget.dto.category.CategoryCreateRequest;
import com.honeybudget.dto.category.CategoryResponse;
import com.honeybudget.dto.category.CategoryUpdateRequest;
import com.honeybudget.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryGroupService categoryGroupService;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryGroup sampleGroup;

    @BeforeEach
    void setUp() {
        sampleGroup = CategoryGroup.builder()
                .groupCode("200")
                .groupName("지출")
                .build();
    }

    @Nested
    @DisplayName("중분류 카테고리 등록 테스트")
    class CreateCategoryTest {

        @Test
        @DisplayName("정상 등록 성공")
        void createCategorySuccess() {
            CategoryCreateRequest request = CategoryCreateRequest.builder()
                    .categoryGroupId(1L)
                    .categoryCode("201")
                    .categoryName("생활비")
                    .build();

            Category category = Category.builder()
                    .group(sampleGroup)
                    .categoryCode("201")
                    .categoryName("생활비")
                    .build();

            given(categoryRepository.existsByCategoryCode("201")).willReturn(false);
            given(categoryGroupService.findGroupById(1L)).willReturn(sampleGroup);
            given(categoryRepository.save(any(Category.class))).willReturn(category);

            CategoryResponse response = categoryService.createCategory(request);

            assertThat(response).isNotNull();
            assertThat(response.getCategoryCode()).isEqualTo("201");
            assertThat(response.getCategoryName()).isEqualTo("생활비");
            assertThat(response.getCategoryGroupName()).isEqualTo("지출");
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("중분류 코드가 중복되면 IllegalArgumentException 발생")
        void createDuplicateCodeThrowsException() {
            CategoryCreateRequest request = CategoryCreateRequest.builder()
                    .categoryGroupId(1L)
                    .categoryCode("201")
                    .categoryName("생활비")
                    .build();

            given(categoryRepository.existsByCategoryCode("201")).willReturn(true);

            assertThatThrownBy(() -> categoryService.createCategory(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("이미 존재하는 중분류 카테고리 코드입니다: 201");
        }
    }

    @Nested
    @DisplayName("중분류 카테고리 수정 테스트")
    class UpdateCategoryTest {

        @Test
        @DisplayName("카테고리 명칭 수정 성공")
        void updateCategoryNameSuccess() {
            Category category = Category.builder()
                    .group(sampleGroup)
                    .categoryCode("201")
                    .categoryName("생활비")
                    .build();

            CategoryUpdateRequest updateRequest = CategoryUpdateRequest.builder()
                    .categoryName("식비")
                    .build();

            given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

            CategoryResponse response = categoryService.updateCategory(1L, updateRequest);

            assertThat(response.getCategoryName()).isEqualTo("식비");
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 ID 수정 시 IllegalArgumentException 발생")
        void updateNonExistentCategoryThrowsException() {
            CategoryUpdateRequest updateRequest = CategoryUpdateRequest.builder()
                    .categoryName("식비")
                    .build();

            given(categoryRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.updateCategory(999L, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("존재하지 않는 카테고리입니다. ID: 999");
        }
    }

    @Nested
    @DisplayName("중분류 카테고리 조회 테스트")
    class GetCategoryTest {

        @Test
        @DisplayName("단건 조회 성공")
        void getCategoryByIdSuccess() {
            Category category = Category.builder()
                    .group(sampleGroup)
                    .categoryCode("201")
                    .categoryName("생활비")
                    .build();

            given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

            CategoryResponse response = categoryService.getCategoryById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getCategoryCode()).isEqualTo("201");
        }

        @Test
        @DisplayName("전체 목록 조회 성공")
        void getAllCategoriesSuccess() {
            Category cat1 = Category.builder().group(sampleGroup).categoryCode("201").categoryName("생활비").build();
            Category cat2 = Category.builder().group(sampleGroup).categoryCode("202").categoryName("고정비").build();

            given(categoryRepository.findAllByOrderByCategoryCodeAsc()).willReturn(List.of(cat1, cat2));

            List<CategoryResponse> responses = categoryService.getAllCategories(null);

            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getCategoryCode()).isEqualTo("201");
            assertThat(responses.get(1).getCategoryCode()).isEqualTo("202");
        }

        @Test
        @DisplayName("대분류 그룹별 목록 필터링 조회 성공")
        void getCategoriesByGroupSuccess() {
            Category cat1 = Category.builder().group(sampleGroup).categoryCode("201").categoryName("생활비").build();

            given(categoryRepository.findAllByGroupIdOrderByCategoryCodeAsc(1L)).willReturn(List.of(cat1));

            List<CategoryResponse> responses = categoryService.getAllCategories(1L);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getCategoryCode()).isEqualTo("201");
        }
    }

    @Nested
    @DisplayName("중분류 카테고리 삭제 테스트")
    class DeleteCategoryTest {

        @Test
        @DisplayName("삭제 성공")
        void deleteCategorySuccess() {
            Category category = Category.builder()
                    .group(sampleGroup)
                    .categoryCode("201")
                    .categoryName("생활비")
                    .build();

            given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

            categoryService.deleteCategory(1L);

            verify(categoryRepository).delete(category);
        }
    }
}
