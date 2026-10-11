package com.honeybudget.service;

import com.honeybudget.domain.Category;
import com.honeybudget.domain.CategoryGroup;
import com.honeybudget.dto.category.CategoryCreateRequest;
import com.honeybudget.dto.category.CategoryResponse;
import com.honeybudget.dto.category.CategoryUpdateRequest;
import com.honeybudget.dto.categorygroup.CategoryGroupResponse;
import com.honeybudget.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryGroupService categoryGroupService;

    /**
     * 중분류 카테고리 등록
     */
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("카테고리 등록 요청 정보가 누락되었습니다.");
        }
        if (request.getCategoryGroupId() == null) {
            throw new IllegalArgumentException("대분류 그룹 ID는 필수입니다.");
        }
        if (request.getCategoryCode() == null || request.getCategoryCode().isBlank()) {
            throw new IllegalArgumentException("중분류 카테고리 코드는 필수입니다.");
        }
        if (request.getCategoryName() == null || request.getCategoryName().isBlank()) {
            throw new IllegalArgumentException("중분류 카테고리 명칭은 필수입니다.");
        }

        String trimmedCode = request.getCategoryCode().trim();
        String trimmedName = request.getCategoryName().trim();

        if (categoryRepository.existsByCategoryCode(trimmedCode)) {
            throw new IllegalArgumentException("이미 존재하는 중분류 카테고리 코드입니다: " + trimmedCode);
        }

        CategoryGroup group = categoryGroupService.findGroupById(request.getCategoryGroupId());

        Category category = Category.builder()
                .group(group)
                .categoryCode(trimmedCode)
                .categoryName(trimmedName)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.from(savedCategory);
    }

    /**
     * 중분류 카테고리 수정 (명칭 및 소속 대분류 그룹 변경)
     */
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request) {
        if (categoryId == null) {
            throw new IllegalArgumentException("카테고리 ID는 필수입니다.");
        }
        if (request == null) {
            throw new IllegalArgumentException("카테고리 수정 요청 정보가 누락되었습니다.");
        }
        if (request.getCategoryName() == null || request.getCategoryName().isBlank()) {
            throw new IllegalArgumentException("중분류 카테고리 명칭은 필수입니다.");
        }

        Category category = findCategoryById(categoryId);

        // 엔티티가 불변 스타일로 정의되어 있으므로 ReflectionUtils를 통해 안전하게 필드 갱신
        Field nameField = ReflectionUtils.findField(Category.class, "categoryName");
        if (nameField != null) {
            ReflectionUtils.makeAccessible(nameField);
            ReflectionUtils.setField(nameField, category, request.getCategoryName().trim());
        }

        if (request.getCategoryGroupId() != null && !request.getCategoryGroupId().equals(category.getGroup().getId())) {
            CategoryGroup newGroup = categoryGroupService.findGroupById(request.getCategoryGroupId());
            Field groupField = ReflectionUtils.findField(Category.class, "group");
            if (groupField != null) {
                ReflectionUtils.makeAccessible(groupField);
                ReflectionUtils.setField(groupField, category, newGroup);
            }
        }

        return CategoryResponse.from(category);
    }

    /**
     * 중분류 카테고리 단건 조회
     */
    public CategoryResponse getCategoryById(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("카테고리 ID는 필수입니다.");
        }
        Category category = findCategoryById(categoryId);
        return CategoryResponse.from(category);
    }

    /**
     * 중분류 카테고리 목록 조회 (대분류 그룹 ID 필터 옵션 지원)
     */
    public List<CategoryResponse> getAllCategories(Long categoryGroupId) {
        List<Category> categories;
        if (categoryGroupId != null) {
            categories = categoryRepository.findAllByGroupIdOrderByCategoryCodeAsc(categoryGroupId);
        } else {
            categories = categoryRepository.findAllByOrderByCategoryCodeAsc();
        }
        return categories.stream()
                .map(CategoryResponse::from)
                .toList();
    }

    /**
     * 중분류 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("카테고리 ID는 필수입니다.");
        }
        Category category = findCategoryById(categoryId);
        categoryRepository.delete(category);
    }

    /**
     * 내부 비즈니스 로직용 카테고리 단건 엔티티 조회
     */
    public Category findCategoryById(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("카테고리 ID는 필수입니다.");
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다. ID: " + categoryId));
    }

    /**
     * 다음 중분류 카테고리 코드 조회 (1단위 증가)
     * 대분류 그룹 코드에 속한 중분류 카테고리 코드 중 최댓값을 기준으로 1단위 증가된 다음 추천 코드(예: 101, 102, 103)를 반환합니다.
     * 해당 대분류 그룹에 등록된 카테고리가 없는 경우 기본 시작 코드(대분류 코드 + 1, 예: 100 -> 101, 200 -> 201)를 반환합니다.
     */
    public CategoryResponse getNextCategoryCode(String categoryGroupCode) {
        if (categoryGroupCode == null || categoryGroupCode.isBlank()) {
            throw new IllegalArgumentException("대분류 그룹 코드는 필수입니다.");
        }

        String trimmedGroupCode = categoryGroupCode.trim();
        int baseCode;
        try {
            baseCode = Integer.parseInt(trimmedGroupCode);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("대분류 그룹 코드는 숫자 형식이어야 합니다: " + categoryGroupCode);
        }

        int maxCode = categoryRepository.findMaxCategoryCodeByGroupCode(trimmedGroupCode);
        int nextCode = (maxCode == 0) ? baseCode + 1 : maxCode + 1;

        return CategoryResponse.fromNextCode(String.valueOf(nextCode));
    }
}
