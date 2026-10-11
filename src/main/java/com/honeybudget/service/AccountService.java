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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final CategoryService categoryService;

    /**
     * 계좌 등록 (Create)
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
     * 계좌 정보 수정 (Update)
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
     * 계좌 단건 조회 (Read)
     */
    public AccountResponse getAccountById(Long accountId) {
        Account account = findAccountById(accountId);
        return AccountResponse.from(account);
    }

    /**
     * 계좌 목록 조회 (Read - 필터 옵션: 활성 여부 useYn, 카테고리 ID)
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
     * 계좌 비활성화 / 해지 처리 (Soft Delete)
     */
    @Transactional
    public void deleteAccount(Long accountId) {
        Account account = findAccountById(accountId);
        account.deactivate();
    }

    /**
     * 내부 비즈니스 로직용 계좌 단건 엔티티 조회
     */
    public Account findAccountById(Long accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("계좌 ID는 필수입니다.");
        }
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계좌입니다. ID: " + accountId));
    }
}
