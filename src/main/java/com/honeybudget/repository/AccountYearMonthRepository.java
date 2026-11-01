package com.honeybudget.repository;

import com.honeybudget.domain.AccountYearMonth;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountYearMonthRepository extends JpaRepository<AccountYearMonth, Long> {

    @EntityGraph(attributePaths = {"account", "account.category", "account.category.group"})
    Optional<AccountYearMonth> findById(Long id);

    @EntityGraph(attributePaths = {"account", "account.category", "account.category.group"})
    List<AccountYearMonth> findAllByYearOrderByAccountIdAsc(Integer year);

    @EntityGraph(attributePaths = {"account", "account.category", "account.category.group"})
    List<AccountYearMonth> findAllByAccountIdOrderByYearDesc(Long accountId);

    @EntityGraph(attributePaths = {"account", "account.category", "account.category.group"})
    Optional<AccountYearMonth> findByAccountIdAndYear(Long accountId, Integer year);

    boolean existsByAccountIdAndYear(Long accountId, Integer year);
}
