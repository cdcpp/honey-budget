package com.honeybudget.repository;

import com.honeybudget.domain.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 중분류 저장소.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(attributePaths = {"group"})
    Optional<Category> findByCategoryCode(String categoryCode);

    boolean existsByCategoryCode(String categoryCode);

    boolean existsByGroupId(Long groupId);

    @EntityGraph(attributePaths = {"group"})
    List<Category> findAllByGroupIdOrderByCategoryCodeAsc(Long groupId);

    @EntityGraph(attributePaths = {"group"})
    List<Category> findAllByOrderByCategoryCodeAsc();

    /** 대분류 내에서 채번을 위해 가장 큰 중분류 코드 숫자를 조회한다. */
    @Query("SELECT COALESCE(MAX(CAST(c.categoryCode AS integer)), 0) " +
           "FROM Category c " +
           "WHERE c.group.categoryGroupCode = :categoryGroupCode")
    Integer findMaxCategoryCodeByGroupCode(@Param("categoryGroupCode") String categoryGroupCode);
}
