package com.honeybudget.service;

import com.honeybudget.domain.Account;
import com.honeybudget.domain.Category;
import com.honeybudget.dto.account.AccountCreateRequest;
import com.honeybudget.dto.account.AccountResponse;
import com.honeybudget.dto.account.AccountUpdateRequest;
import com.honeybudget.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 계좌 등록, 수정, 조회, 삭제를 처리하는 비즈니스 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final CategoryService categoryService;

    /**
     * 신규 계좌를 등록한다.
     * 필수 필드(명칭, 금융기관명, 카테고리 ID)가 누락되면 IllegalArgumentException을 던진다.
     */
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("계좌 등록 요청 정보가 누락되었습니다.");
        }
        if (request.getAccountName() == null || request.getAccountName().isBlank()) {
            throw new IllegalArgumentException("계좌 명칭은 필수입니다.");
        }
        if (request.getInstitutionName() == null || request.getInstitutionName().isBlank()) {
            throw new IllegalArgumentException("금융기관명은 필수입니다.");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("카테고리 ID는 필수입니다.");
        }

        String trimmedInstitution = request.getInstitutionName().trim();
        Category category = categoryService.findCategoryById(request.getCategoryId());

        Account account = Account.builder()
                .accountName(request.getAccountName().trim())
                .institutionName(trimmedInstitution)
                .category(category)
                .memo(request.getMemo() != null ? request.getMemo().trim() : null)
                .build();

        Account savedAccount = accountRepository.save(account);
        return AccountResponse.from(savedAccount);
    }

    /**
     * 계좌 정보(명칭, 메모, 목적 카테고리)를 수정한다.
     */
    @Transactional
    public AccountResponse updateAccount(Long accountId, AccountUpdateRequest request) {
        if (accountId == null) {
            throw new IllegalArgumentException("계좌 ID는 필수입니다.");
        }
        if (request == null) {
            throw new IllegalArgumentException("계좌 수정 요청 정보가 누락되었습니다.");
        }
        if (request.getAccountName() == null || request.getAccountName().isBlank()) {
            throw new IllegalArgumentException("계좌 명칭은 필수입니다.");
        }

        Account account = findAccountById(accountId);

        // 계좌 기본 정보 수정
        account.updateAccountInfo(
                request.getAccountName().trim(),
                request.getMemo() != null ? request.getMemo().trim() : null
        );

        // 목적 카테고리 변경 요청이 있는 경우
        if (request.getCategoryId() != null && (account.getCategory() == null || !request.getCategoryId().equals(account.getCategory().getId()))) {
            Category newCategory = categoryService.findCategoryById(request.getCategoryId());
            account.changeCategory(newCategory);
        }

        return AccountResponse.from(account);
    }

    /**
     * 계좌 단건 정보를 조회한다.
     */
    public AccountResponse getAccountById(Long accountId) {
        Account account = findAccountById(accountId);
        return AccountResponse.from(account);
    }

    /**
     * 조건에 맞는 계좌 목록을 최신순으로 조회한다.
     *
     * @param useYn      null이면 활성 여부 무관 조회
     * @param categoryId null이면 전체 카테고리 대상 조회
     */
    public List<AccountResponse> getAllAccounts(Boolean useYn, Long categoryId) {
        List<Account> accounts;
        if (categoryId != null && useYn != null) {
            accounts = accountRepository.findAllByCategoryIdAndUseYnOrderByIdDesc(categoryId, useYn);
        } else if (categoryId != null) {
            accounts = accountRepository.findAllByCategoryIdOrderByIdDesc(categoryId);
        } else if (useYn != null) {
            accounts = accountRepository.findAllByUseYnOrderByIdDesc(useYn);
        } else {
            accounts = accountRepository.findAllByOrderByIdDesc();
        }

        return accounts.stream()
                .map(AccountResponse::from)
                .toList();
    }

    /**
     * 계좌를 삭제한다.
     */
    @Transactional
    public void deleteAccount(Long accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("계좌 ID는 필수입니다.");
        }
        Account account = findAccountById(accountId);
        accountRepository.delete(account);
    }

    /**
     * 계좌 단건 엔티티를 조회한다. 존재하지 않으면 IllegalArgumentException을 던진다.
     */
    public Account findAccountById(Long accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("계좌 ID는 필수입니다.");
        }
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계좌입니다. ID: " + accountId));
    }
}
