package com.honeybudget.repository;

import com.honeybudget.domain.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long> {

    Optional<CategoryGroup> findByCategoryGroupCode(String categoryGroupCode);

    boolean existsByCategoryGroupCode(String categoryGroupCode);

    boolean existsByCategoryGroupName(String categoryGroupName);

    List<CategoryGroup> findAllByOrderByCategoryGroupCodeAsc();
}
