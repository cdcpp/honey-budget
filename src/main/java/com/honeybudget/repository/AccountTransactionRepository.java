package com.honeybudget.repository;

import com.honeybudget.domain.AccountTransaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

    @EntityGraph(attributePaths = {"account"})
    Optional<AccountTransaction> findById(Long id);

    @EntityGraph(attributePaths = {"account"})
    List<AccountTransaction> findAllByOrderByTransactionDateDescIdDesc();

    @EntityGraph(attributePaths = {"account"})
    List<AccountTransaction> findAllByAccountIdOrderByTransactionDateDescIdDesc(Long accountId);

    @EntityGraph(attributePaths = {"account"})
    List<AccountTransaction> findAllByYearMonthOrderByTransactionDateDescIdDesc(YearMonth yearMonth);

    @EntityGraph(attributePaths = {"account"})
    List<AccountTransaction> findAllByAccountIdAndYearMonthOrderByTransactionDateDescIdDesc(Long accountId, YearMonth yearMonth);
}
