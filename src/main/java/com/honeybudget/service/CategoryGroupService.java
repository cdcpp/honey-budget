package com.honeybudget.service;

import com.honeybudget.domain.CategoryGroup;
import com.honeybudget.dto.categorygroup.CategoryGroupCreateRequest;
import com.honeybudget.dto.categorygroup.CategoryGroupResponse;
import com.honeybudget.dto.categorygroup.CategoryGroupUpdateRequest;
import com.honeybudget.repository.CategoryGroupRepository;
import com.honeybudget.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 카테고리 대분류 그룹의 등록, 수정, 조회, 삭제를 처리하는 비즈니스 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryGroupService {

    private final CategoryGroupRepository categoryGroupRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 대분류 그룹을 등록한다. 코드가 중복되면 IllegalArgumentException을 던진다.
     */
    @Transactional
    public CategoryGroupResponse createCategoryGroup(CategoryGroupCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("대분류 그룹 등록 요청 정보가 누락되었습니다.");
        }
        if (request.getCategoryGroupCode() == null || request.getCategoryGroupCode().isBlank()) {
            throw new IllegalArgumentException("대분류 그룹 코드는 필수입니다.");
        }
        if (request.getCategoryGroupName() == null || request.getCategoryGroupName().isBlank()) {
            throw new IllegalArgumentException("대분류 그룹 명칭은 필수입니다.");
        }

        String trimmedCode = request.getCategoryGroupCode().trim();
        String trimmedName = request.getCategoryGroupName().trim();

        if (categoryGroupRepository.existsByCategoryGroupCode(trimmedCode)) {
            throw new IllegalArgumentException("이미 존재하는 대분류 그룹 코드입니다: " + trimmedCode);
        }

        CategoryGroup group = CategoryGroup.builder()
                .groupCode(trimmedCode)
                .groupName(trimmedName)
                .build();

        CategoryGroup savedGroup = categoryGroupRepository.save(group);
        return CategoryGroupResponse.from(savedGroup);
    }

    /**
     * 대분류 그룹 명칭을 수정한다.
     */
    @Transactional
    public CategoryGroupResponse updateCategoryGroup(Long groupId, CategoryGroupUpdateRequest request) {
        if (groupId == null) {
            throw new IllegalArgumentException("대분류 그룹 ID는 필수입니다.");
        }
        if (request == null) {
            throw new IllegalArgumentException("대분류 그룹 수정 요청 정보가 누락되었습니다.");
        }
        if (request.getCategoryGroupName() == null || request.getCategoryGroupName().isBlank()) {
            throw new IllegalArgumentException("대분류 그룹 명칭은 필수입니다.");
        }

        CategoryGroup group = findGroupById(groupId);
        group.updateGroupName(request.getCategoryGroupName().trim());

        return CategoryGroupResponse.from(group);
    }

    /**
     * 대분류 그룹 단건 정보를 조회한다.
     */
    public CategoryGroupResponse getCategoryGroupById(Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("대분류 그룹 ID는 필수입니다.");
        }
        CategoryGroup group = findGroupById(groupId);
        return CategoryGroupResponse.from(group);
    }

    /**
     * 모든 대분류 그룹 목록을 코드 오름차순으로 조회한다.
     */
    public List<CategoryGroupResponse> getAllCategoryGroups() {
        return categoryGroupRepository.findAllByOrderByCategoryGroupCodeAsc().stream()
                .map(CategoryGroupResponse::from)
                .toList();
    }

    /**
     * 다음 추천 대분류 그룹 코드를 조회한다. (최댓값 + 100 단위, 없으면 기본값 100)
     */
    public CategoryGroupResponse getNextCategoryGroupCode() {
        int maxCode = categoryGroupRepository.findMaxCategoryGroupCode();
        int nextCode = (maxCode == 0) ? 100 : maxCode + 100;

        return CategoryGroupResponse.fromNextCode(String.valueOf(nextCode));
    }

    /**
     * 대분류 그룹을 삭제한다. 하위 중분류가 남아 있으면 삭제할 수 없다.
     */
    @Transactional
    public void deleteCategoryGroup(Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("대분류 그룹 ID는 필수입니다.");
        }
        CategoryGroup group = findGroupById(groupId);

        // 하위 중분류 카테고리가 존재하는 경우 삭제 불가 예외 처리
        if (categoryRepository.existsByGroupId(groupId)) {
            throw new IllegalArgumentException("해당 대분류 그룹에 속한 하위 카테고리가 존재하여 삭제할 수 없습니다. ID: " + groupId);
        }

        categoryGroupRepository.delete(group);
    }

    /**
     * 대분류 그룹 엔티티를 조회한다. 존재하지 않으면 IllegalArgumentException을 던진다.
     */
    public CategoryGroup findGroupById(Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("대분류 그룹 ID는 필수입니다.");
        }
        return categoryGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대분류 그룹입니다. ID: " + groupId));
    }
}
