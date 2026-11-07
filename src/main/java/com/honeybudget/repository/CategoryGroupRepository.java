package com.honeybudget.repository;

import com.honeybudget.domain.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 대분류 그룹 저장소.
 */
public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long> {

    Optional<CategoryGroup> findByCategoryGroupCode(String categoryGroupCode);

    boolean existsByCategoryGroupCode(String categoryGroupCode);

    boolean existsByCategoryGroupName(String categoryGroupName);

    List<CategoryGroup> findAllByOrderByCategoryGroupCodeAsc();

    /** 채번을 위해 현재 가장 큰 대분류 코드 숫자를 조회한다. */
    @Query("SELECT COALESCE(MAX(CAST(cg.categoryGroupCode AS integer)),0) FROM CategoryGroup cg")
    Integer findMaxCategoryGroupCode();

}
