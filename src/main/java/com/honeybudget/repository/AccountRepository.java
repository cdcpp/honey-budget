package com.honeybudget.repository;

import com.honeybudget.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @EntityGraph(attributePaths = {"category", "category.group"})
    Optional<Account> findById(Long id);

    @EntityGraph(attributePaths = {"category", "category.group"})
    List<Account> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = {"category", "category.group"})
    List<Account> findAllByUseYnOrderByIdDesc(Boolean useYn);

    @EntityGraph(attributePaths = {"category", "category.group"})
    List<Account> findAllByCategoryIdOrderByIdDesc(Long categoryId);

    @EntityGraph(attributePaths = {"category", "category.group"})
    List<Account> findAllByCategoryIdAndUseYnOrderByIdDesc(Long categoryId, Boolean useYn);

    boolean existsByCategoryId(Long categoryId);
}
