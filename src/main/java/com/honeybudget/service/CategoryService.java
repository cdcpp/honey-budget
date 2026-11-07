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

/**
 * 카테고리(중분류)의 등록, 수정, 조회, 삭제를 처리하는 비즈니스 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryGroupService categoryGroupService;

    /**
     * 신규 중분류 카테고리를 등록한다. 코드가 중복되면 IllegalArgumentException을 던진다.
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
     * 중분류 카테고리 명칭 및 소속 대분류 그룹을 수정한다.
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
     * 중분류 카테고리 단건 정보를 조회한다.
     */
    public CategoryResponse getCategoryById(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("카테고리 ID는 필수입니다.");
        }
        Category category = findCategoryById(categoryId);
        return CategoryResponse.from(category);
    }

    /**
     * 중분류 카테고리 목록을 코드순으로 조회한다. 대분류 그룹 ID로 필터링할 수 있다.
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
     * 중분류 카테고리를 삭제한다.
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
     * 카테고리 단건 엔티티를 조회한다. 존재하지 않으면 IllegalArgumentException을 던진다.
     */
    public Category findCategoryById(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("카테고리 ID는 필수입니다.");
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다. ID: " + categoryId));
    }

    /**
     * 대분류 그룹 내 다음 추천 중분류 코드를 조회한다. (최댓값 + 1, 없으면 그룹시작코드 + 1)
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
