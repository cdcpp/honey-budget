package com.honeybudget.service;

import com.honeybudget.domain.CategoryGroup;
import com.honeybudget.dto.categorygroup.CategoryGroupCreateRequest;
import com.honeybudget.dto.categorygroup.CategoryGroupResponse;
import com.honeybudget.dto.categorygroup.CategoryGroupUpdateRequest;
import com.honeybudget.repository.CategoryGroupRepository;
import com.honeybudget.repository.CategoryRepository;
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
class CategoryGroupServiceTest {

    @Mock
    private CategoryGroupRepository categoryGroupRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryGroupService categoryGroupService;

    @Nested
    @DisplayName("대분류 그룹 등록 테스트")
    class CreateCategoryGroupTest {

        @Test
        @DisplayName("정상 등록 성공")
        void createCategoryGroupSuccess() {
            CategoryGroupCreateRequest request = CategoryGroupCreateRequest.builder()
                    .categoryGroupCode("100")
                    .categoryGroupName("수입")
                    .build();

            CategoryGroup group = CategoryGroup.builder()
                    .groupCode("100")
                    .groupName("수입")
                    .build();

            given(categoryGroupRepository.existsByCategoryGroupCode("100")).willReturn(false);
            given(categoryGroupRepository.save(any(CategoryGroup.class))).willReturn(group);

            CategoryGroupResponse response = categoryGroupService.createCategoryGroup(request);

            assertThat(response).isNotNull();
            assertThat(response.getCategoryGroupCode()).isEqualTo("100");
            assertThat(response.getCategoryGroupName()).isEqualTo("수입");
            verify(categoryGroupRepository).save(any(CategoryGroup.class));
        }

        @Test
        @DisplayName("요청 객체가 null이면 IllegalArgumentException 발생")
        void createWithNullRequestThrowsException() {
            assertThatThrownBy(() -> categoryGroupService.createCategoryGroup(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("대분류 그룹 등록 요청 정보가 누락되었습니다.");
        }

        @Test
        @DisplayName("대분류 코드가 중복되면 IllegalArgumentException 발생")
        void createDuplicateCodeThrowsException() {
            CategoryGroupCreateRequest request = CategoryGroupCreateRequest.builder()
                    .categoryGroupCode("100")
                    .categoryGroupName("수입")
                    .build();

            given(categoryGroupRepository.existsByCategoryGroupCode("100")).willReturn(true);

            assertThatThrownBy(() -> categoryGroupService.createCategoryGroup(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("이미 존재하는 대분류 그룹 코드입니다: 100");
        }
    }

    @Nested
    @DisplayName("대분류 그룹 수정 테스트")
    class UpdateCategoryGroupTest {

        @Test
        @DisplayName("대분류 명칭 정상 수정 성공")
        void updateCategoryGroupSuccess() {
            CategoryGroup group = CategoryGroup.builder()
                    .groupCode("100")
                    .groupName("수입")
                    .build();

            CategoryGroupUpdateRequest updateRequest = CategoryGroupUpdateRequest.builder()
                    .categoryGroupName("수입 (급여/기타)")
                    .build();

            given(categoryGroupRepository.findById(1L)).willReturn(Optional.of(group));

            CategoryGroupResponse response = categoryGroupService.updateCategoryGroup(1L, updateRequest);

            assertThat(response.getCategoryGroupName()).isEqualTo("수입 (급여/기타)");
        }

        @Test
        @DisplayName("존재하지 않는 대분류 ID 수정 시 IllegalArgumentException 발생")
        void updateNonExistentGroupThrowsException() {
            CategoryGroupUpdateRequest updateRequest = CategoryGroupUpdateRequest.builder()
                    .categoryGroupName("수입 (급여/기타)")
                    .build();

            given(categoryGroupRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> categoryGroupService.updateCategoryGroup(999L, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("존재하지 않는 대분류 그룹입니다. ID: 999");
        }
    }

    @Nested
    @DisplayName("대분류 그룹 조회 테스트")
    class GetCategoryGroupTest {

        @Test
        @DisplayName("단건 조회 성공")
        void getCategoryGroupByIdSuccess() {
            CategoryGroup group = CategoryGroup.builder()
                    .groupCode("100")
                    .groupName("수입")
                    .build();

            given(categoryGroupRepository.findById(1L)).willReturn(Optional.of(group));

            CategoryGroupResponse response = categoryGroupService.getCategoryGroupById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getCategoryGroupCode()).isEqualTo("100");
        }

        @Test
        @DisplayName("전체 목록 조회 성공")
        void getAllCategoryGroupsSuccess() {
            CategoryGroup group1 = CategoryGroup.builder().groupCode("100").groupName("수입").build();
            CategoryGroup group2 = CategoryGroup.builder().groupCode("200").groupName("지출").build();

            given(categoryGroupRepository.findAllByOrderByCategoryGroupCodeAsc()).willReturn(List.of(group1, group2));

            List<CategoryGroupResponse> responses = categoryGroupService.getAllCategoryGroups();

            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getCategoryGroupCode()).isEqualTo("100");
            assertThat(responses.get(1).getCategoryGroupCode()).isEqualTo("200");
        }

        @Test
        @DisplayName("등록된 대분류 그룹이 없을 때 다음 코드는 100을 반환")
        void getNextCategoryGroupCodeDefaultWhenEmpty() {
            given(categoryGroupRepository.findMaxCategoryGroupCode()).willReturn(0);

            CategoryGroupResponse response = categoryGroupService.getNextCategoryGroupCode();

            assertThat(response).isNotNull();
            assertThat(response.getNextCode()).isEqualTo("100");
        }

        @Test
        @DisplayName("등록된 대분류 그룹이 있을 때 최댓값 기준 100 증가된 코드 반환 (예: 200 -> 300)")
        void getNextCategoryGroupCodeIncrementBy100() {
            given(categoryGroupRepository.findMaxCategoryGroupCode()).willReturn(200);

            CategoryGroupResponse response = categoryGroupService.getNextCategoryGroupCode();

            assertThat(response).isNotNull();
            assertThat(response.getNextCode()).isEqualTo("300");
        }
    }

    @Nested
    @DisplayName("대분류 그룹 삭제 테스트")
    class DeleteCategoryGroupTest {

        @Test
        @DisplayName("하위 카테고리가 없는 경우 정상 삭제 성공")
        void deleteCategoryGroupSuccess() {
            CategoryGroup group = CategoryGroup.builder()
                    .groupCode("100")
                    .groupName("수입")
                    .build();

            given(categoryGroupRepository.findById(1L)).willReturn(Optional.of(group));
            given(categoryRepository.existsByGroupId(1L)).willReturn(false);

            categoryGroupService.deleteCategoryGroup(1L);

            verify(categoryGroupRepository).delete(group);
        }

        @Test
        @DisplayName("하위 카테고리가 남아있는 경우 삭제 시 IllegalArgumentException 발생")
        void deleteGroupWithChildrenThrowsException() {
            CategoryGroup group = CategoryGroup.builder()
                    .groupCode("100")
                    .groupName("수입")
                    .build();

            given(categoryGroupRepository.findById(1L)).willReturn(Optional.of(group));
            given(categoryRepository.existsByGroupId(1L)).willReturn(true);

            assertThatThrownBy(() -> categoryGroupService.deleteCategoryGroup(1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("하위 카테고리가 존재하여 삭제할 수 없습니다");
        }
    }
}
