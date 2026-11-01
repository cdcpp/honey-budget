package com.honeybudget.service;

import com.honeybudget.domain.Account;
import com.honeybudget.domain.AccountYearMonth;
import com.honeybudget.dto.accountyearmonth.AccountYearMonthCreateRequest;
import com.honeybudget.dto.accountyearmonth.AccountYearMonthResponse;
import com.honeybudget.dto.accountyearmonth.AccountYearMonthUpdateRequest;
import com.honeybudget.dto.accountyearmonth.AnnualStatementResponse;
import com.honeybudget.repository.AccountRepository;
import com.honeybudget.repository.AccountYearMonthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountYearMonthService {

    private final AccountYearMonthRepository accountYearMonthRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    /**
     * 계좌 연간 월별 금액 등록 (Create) - 계좌+연도당 1건
     */
    @Transactional
    public AccountYearMonthResponse create(AccountYearMonthCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("등록 요청 정보가 누락되었습니다.");
        }
        if (request.getAccountId() == null) {
            throw new IllegalArgumentException("계좌 ID는 필수입니다.");
        }
        if (request.getYear() == null) {
            throw new IllegalArgumentException("연도는 필수입니다.");
        }
        Account account = accountService.findAccountById(request.getAccountId());

        if (accountYearMonthRepository.existsByAccountIdAndYear(account.getId(), request.getYear())) {
            throw new IllegalArgumentException(
                    "이미 등록된 계좌/연도입니다. 계좌 ID: " + account.getId() + ", 연도: " + request.getYear());
        }

        AccountYearMonth entity = AccountYearMonth.builder()
                .account(account)
                .year(request.getYear())
                .build();
        applyAmounts(entity, request.getAmounts());

        return AccountYearMonthResponse.from(accountYearMonthRepository.save(entity));
    }

    /**
     * 월별 금액 수정 (Update) - 요청에 포함된 월만 수정
     */
    @Transactional
    public AccountYearMonthResponse update(Long id, AccountYearMonthUpdateRequest request) {
        if (request == null || request.getAmounts() == null || request.getAmounts().isEmpty()) {
            throw new IllegalArgumentException("수정할 월별 금액은 필수입니다.");
        }
        AccountYearMonth entity = findById(id);
        applyAmounts(entity, request.getAmounts());
        return AccountYearMonthResponse.from(entity);
    }

    /**
     * 단건 조회 (Read)
     */
    public AccountYearMonthResponse getById(Long id) {
        return AccountYearMonthResponse.from(findById(id));
    }

    /**
     * 특정 연도 기준 전체 계좌 월별 데이터 조회 (연도별 조회)
     * 활성 계좌(useYn = true) 중 해당 연도 데이터가 아직 없는 경우 자동으로 기본값(0원) 생성
     */
    @Transactional
    public List<AccountYearMonthResponse> getByYear(Integer year) {
        int targetYear = (year != null) ? year : Year.now().getValue();

        List<Account> activeAccounts = accountRepository.findAllByUseYnOrderByIdAsc(true);
        List<AccountYearMonth> existingList = accountYearMonthRepository.findAllByYearOrderByAccountIdAsc(targetYear);

        Set<Long> existingAccountIds = existingList.stream()
                .map(aym -> aym.getAccount().getId())
                .collect(Collectors.toSet());

        List<AccountYearMonth> newEntries = new ArrayList<>();
        for (Account account : activeAccounts) {
            if (!existingAccountIds.contains(account.getId())) {
                AccountYearMonth newRecord = AccountYearMonth.builder()
                        .account(account)
                        .year(targetYear)
                        .build();
                newEntries.add(newRecord);
            }
        }

        if (!newEntries.isEmpty()) {
            accountYearMonthRepository.saveAll(newEntries);
            existingList = accountYearMonthRepository.findAllByYearOrderByAccountIdAsc(targetYear);
        }

        return existingList.stream()
                .map(AccountYearMonthResponse::from)
                .toList();
    }

    /**
     * 연간 수입/저축/지출 명세표 종합 조회 (대분류 그룹별 소계 및 순수익 포함)
     */
    @Transactional
    public AnnualStatementResponse getAnnualStatement(Integer year) {
        int targetYear = (year != null) ? year : Year.now().getValue();
        List<AccountYearMonthResponse> items = getByYear(targetYear);
        return AnnualStatementResponse.of(targetYear, items);
    }

    /**
     * 목록 조회 (Read) - year, accountId 필터
     */
    @Transactional
    public List<AccountYearMonthResponse> getAll(Integer year, Long accountId) {
        if (accountId != null && year != null) {
            Optional<AccountYearMonth> opt = accountYearMonthRepository.findByAccountIdAndYear(accountId, year);
            if (opt.isPresent()) {
                return List.of(AccountYearMonthResponse.from(opt.get()));
            }
            // 계좌가 존재하고 활성 상태라면 해당 연도 데이터 자동 생성
            Account account = accountService.findAccountById(accountId);
            AccountYearMonth created = accountYearMonthRepository.save(
                    AccountYearMonth.builder().account(account).year(year).build());
            return List.of(AccountYearMonthResponse.from(created));
        } else if (accountId != null) {
            return accountYearMonthRepository.findAllByAccountIdOrderByYearDesc(accountId)
                    .stream().map(AccountYearMonthResponse::from).toList();
        } else {
            return getByYear(year);
        }
    }

    private void applyAmounts(AccountYearMonth entity, Map<Integer, BigDecimal> amounts) {
        if (amounts == null) {
            return;
        }
        amounts.forEach((month, amount) -> {
            if (month == null) {
                throw new IllegalArgumentException("월은 필수입니다.");
            }
            entity.updateAmount(month, amount);
        });
    }

    private AccountYearMonth findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID는 필수입니다.");
        }
        return accountYearMonthRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계좌 월별 금액입니다. ID: " + id));
    }
}
