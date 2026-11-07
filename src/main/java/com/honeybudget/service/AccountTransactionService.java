package com.honeybudget.service;

import com.honeybudget.domain.Account;
import com.honeybudget.domain.AccountTransaction;
import com.honeybudget.dto.accounttransaction.AccountTransactionCreateRequest;
import com.honeybudget.dto.accounttransaction.AccountTransactionResponse;
import com.honeybudget.dto.accounttransaction.AccountTransactionUpdateRequest;
import com.honeybudget.repository.AccountTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 계좌 거래 내역 등록, 수정, 조회, 삭제를 처리하는 비즈니스 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountTransactionService {

    private final AccountTransactionRepository accountTransactionRepository;
    private final AccountService accountService;

    /**
     * 계좌 거래 내역을 등록한다. 거래 일자로부터 연월(yearMonth)을 자동 계산한다.
     */
    @Transactional
    public AccountTransactionResponse create(AccountTransactionCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("거래 내역 등록 요청 정보가 누락되었습니다.");
        }
        if (request.getAccountId() == null) {
            throw new IllegalArgumentException("계좌 ID는 필수입니다.");
        }

        Account account = accountService.findAccountById(request.getAccountId());

        // 필수값(일자/금액/내용) 검증 및 yearMonth 자동 계산은 엔티티 생성자에서 수행
        AccountTransaction transaction = AccountTransaction.builder()
                .account(account)
                .transactionDate(request.getTransactionDate())
                .amount(request.getAmount())
                .description(request.getDescription())
                .memo(request.getMemo())
                .build();

        AccountTransaction saved = accountTransactionRepository.save(transaction);
        return AccountTransactionResponse.from(saved);
    }

    /**
     * 거래 내역을 수정한다. 거래 일자가 바뀌면 귀속 연월도 자동 갱신된다.
     */
    @Transactional
    public AccountTransactionResponse update(Long transactionId, AccountTransactionUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("거래 내역 수정 요청 정보가 누락되었습니다.");
        }

        AccountTransaction transaction = findTransactionById(transactionId);
        transaction.updateTransaction(
                request.getTransactionDate(),
                request.getAmount(),
                request.getDescription(),
                request.getMemo()
        );

        return AccountTransactionResponse.from(transaction);
    }

    /**
     * 거래 내역 단건 정보를 조회한다.
     */
    public AccountTransactionResponse getById(Long transactionId) {
        return AccountTransactionResponse.from(findTransactionById(transactionId));
    }

    /**
     * 조건에 맞는 거래 내역 목록을 거래 일자 최신순으로 조회한다.
     */
    public List<AccountTransactionResponse> getAll(Long accountId, String yearMonth) {
        YearMonth ym = parseYearMonth(yearMonth);

        List<AccountTransaction> transactions;
        if (accountId != null && ym != null) {
            transactions = accountTransactionRepository.findAllByAccountIdAndYearMonthOrderByTransactionDateDescIdDesc(accountId, ym);
        } else if (accountId != null) {
            transactions = accountTransactionRepository.findAllByAccountIdOrderByTransactionDateDescIdDesc(accountId);
        } else if (ym != null) {
            transactions = accountTransactionRepository.findAllByYearMonthOrderByTransactionDateDescIdDesc(ym);
        } else {
            transactions = accountTransactionRepository.findAllByOrderByTransactionDateDescIdDesc();
        }

        return transactions.stream()
                .map(AccountTransactionResponse::from)
                .toList();
    }

    /**
     * 거래 내역을 삭제한다.
     */
    @Transactional
    public void delete(Long transactionId) {
        AccountTransaction transaction = findTransactionById(transactionId);
        accountTransactionRepository.delete(transaction);
    }

    /**
     * 거래 내역 단건 엔티티를 조회한다. 존재하지 않으면 IllegalArgumentException을 던진다.
     */
    public AccountTransaction findTransactionById(Long transactionId) {
        if (transactionId == null) {
            throw new IllegalArgumentException("거래 내역 ID는 필수입니다.");
        }
        return accountTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래 내역입니다. ID: " + transactionId));
    }

    private YearMonth parseYearMonth(String yearMonth) {
        if (yearMonth == null || yearMonth.isBlank()) {
            return null;
        }
        try {
            return YearMonth.parse(yearMonth.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("연월은 YYYY-MM 형식이어야 합니다. 입력값: " + yearMonth);
        }
    }
}
