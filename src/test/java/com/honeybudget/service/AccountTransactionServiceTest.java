package com.honeybudget.service;

import com.honeybudget.domain.Account;
import com.honeybudget.domain.AccountTransaction;
import com.honeybudget.domain.Category;
import com.honeybudget.domain.CategoryGroup;
import com.honeybudget.dto.accounttransaction.AccountTransactionCreateRequest;
import com.honeybudget.dto.accounttransaction.AccountTransactionResponse;
import com.honeybudget.dto.accounttransaction.AccountTransactionUpdateRequest;
import com.honeybudget.repository.AccountTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * AccountTransactionService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class AccountTransactionServiceTest {

    @Mock
    private AccountTransactionRepository accountTransactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountTransactionService accountTransactionService;

    private Account sampleAccount;

    @BeforeEach
    void setUp() {
        CategoryGroup group = CategoryGroup.builder()
                .groupCode("200")
                .groupName("지출")
                .build();

        Category category = Category.builder()
                .group(group)
                .categoryCode("201")
                .categoryName("생활비 통장")
                .build();

        sampleAccount = Account.builder()
                .accountName("토스 생활비 통장")
                .institutionName("토스뱅크")
                .category(category)
                .build();
    }

    private AccountTransaction createTransaction(LocalDate date, long amount, String description) {
        return AccountTransaction.builder()
                .account(sampleAccount)
                .transactionDate(date)
                .amount(BigDecimal.valueOf(amount))
                .description(description)
                .build();
    }

    @Nested
    @DisplayName("거래 내역 등록 테스트")
    class CreateTest {

        @Test
        @DisplayName("정상 등록 성공 - yearMonth는 거래 일자로부터 자동 계산된다")
        void createSuccess() {
            AccountTransactionCreateRequest request = AccountTransactionCreateRequest.builder()
                    .accountId(1L)
                    .transactionDate(LocalDate.of(2025, 1, 15))
                    .amount(BigDecimal.valueOf(35000))
                    .description("  이마트 장보기  ")
                    .memo("주말 장보기")
                    .build();

            given(accountService.findAccountById(1L)).willReturn(sampleAccount);
            given(accountTransactionRepository.save(any(AccountTransaction.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            AccountTransactionResponse response = accountTransactionService.create(request);

            assertThat(response).isNotNull();
            assertThat(response.getAccountName()).isEqualTo("토스 생활비 통장");
            assertThat(response.getYearMonth()).isEqualTo("2025-01");
            assertThat(response.getTransactionDate()).isEqualTo(LocalDate.of(2025, 1, 15));
            assertThat(response.getAmount()).isEqualByComparingTo("35000");
            assertThat(response.getDescription()).isEqualTo("이마트 장보기");
            assertThat(response.getMemo()).isEqualTo("주말 장보기");
            verify(accountTransactionRepository).save(any(AccountTransaction.class));
        }

        @Test
        @DisplayName("요청 정보 누락 시 IllegalArgumentException 발생")
        void createNullRequestThrowsException() {
            assertThatThrownBy(() -> accountTransactionService.create(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("거래 내역 등록 요청 정보가 누락되었습니다.");
        }

        @Test
        @DisplayName("존재하지 않는 계좌 ID로 등록 시 IllegalArgumentException 발생")
        void createWithNonExistentAccountThrowsException() {
            AccountTransactionCreateRequest request = AccountTransactionCreateRequest.builder()
                    .accountId(999L)
                    .transactionDate(LocalDate.of(2025, 1, 15))
                    .amount(BigDecimal.valueOf(1000))
                    .description("편의점")
                    .build();

            given(accountService.findAccountById(999L))
                    .willThrow(new IllegalArgumentException("존재하지 않는 계좌입니다. ID: 999"));

            assertThatThrownBy(() -> accountTransactionService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("존재하지 않는 계좌입니다. ID: 999");
            verify(accountTransactionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("거래 내역 수정 테스트")
    class UpdateTest {

        @Test
        @DisplayName("거래 일자 변경 시 yearMonth도 함께 변경된다")
        void updateSuccess() {
            AccountTransaction transaction = createTransaction(LocalDate.of(2025, 1, 15), 35000, "이마트 장보기");

            AccountTransactionUpdateRequest request = AccountTransactionUpdateRequest.builder()
                    .transactionDate(LocalDate.of(2025, 2, 3))
                    .amount(BigDecimal.valueOf(42000))
                    .description("GS칼텍스 주유")
                    .memo("출장 주유")
                    .build();

            given(accountTransactionRepository.findById(1L)).willReturn(Optional.of(transaction));

            AccountTransactionResponse response = accountTransactionService.update(1L, request);

            assertThat(response.getTransactionDate()).isEqualTo(LocalDate.of(2025, 2, 3));
            assertThat(response.getYearMonth()).isEqualTo("2025-02");
            assertThat(response.getAmount()).isEqualByComparingTo("42000");
            assertThat(response.getDescription()).isEqualTo("GS칼텍스 주유");
            assertThat(response.getMemo()).isEqualTo("출장 주유");
        }

        @Test
        @DisplayName("존재하지 않는 거래 내역 수정 시 IllegalArgumentException 발생")
        void updateNotFoundThrowsException() {
            AccountTransactionUpdateRequest request = AccountTransactionUpdateRequest.builder()
                    .amount(BigDecimal.valueOf(1000))
                    .build();

            given(accountTransactionRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> accountTransactionService.update(999L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("존재하지 않는 거래 내역입니다. ID: 999");
        }
    }

    @Nested
    @DisplayName("거래 내역 조회 테스트")
    class GetTest {

        @Test
        @DisplayName("단건 조회 성공")
        void getByIdSuccess() {
            AccountTransaction transaction = createTransaction(LocalDate.of(2025, 1, 15), 35000, "이마트 장보기");
            given(accountTransactionRepository.findById(1L)).willReturn(Optional.of(transaction));

            AccountTransactionResponse response = accountTransactionService.getById(1L);

            assertThat(response.getDescription()).isEqualTo("이마트 장보기");
        }

        @Test
        @DisplayName("필터 없이 전체 조회")
        void getAllWithoutFilter() {
            given(accountTransactionRepository.findAllByOrderByTransactionDateDescIdDesc()).willReturn(List.of(
                    createTransaction(LocalDate.of(2025, 1, 15), 1000, "A"),
                    createTransaction(LocalDate.of(2025, 2, 15), 2000, "B")
            ));

            List<AccountTransactionResponse> responses = accountTransactionService.getAll(null, null);

            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("계좌 ID 필터로 조회")
        void getAllByAccountId() {
            given(accountTransactionRepository.findAllByAccountIdOrderByTransactionDateDescIdDesc(1L))
                    .willReturn(List.of(createTransaction(LocalDate.of(2025, 1, 15), 1000, "A")));

            List<AccountTransactionResponse> responses = accountTransactionService.getAll(1L, null);

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("월(yearMonth) 필터로 조회")
        void getAllByYearMonth() {
            given(accountTransactionRepository.findAllByYearMonthOrderByTransactionDateDescIdDesc(YearMonth.of(2025, 1)))
                    .willReturn(List.of(createTransaction(LocalDate.of(2025, 1, 15), 1000, "A")));

            List<AccountTransactionResponse> responses = accountTransactionService.getAll(null, "2025-01");

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("계좌 ID + 월(yearMonth) 필터로 조회")
        void getAllByAccountIdAndYearMonth() {
            given(accountTransactionRepository.findAllByAccountIdAndYearMonthOrderByTransactionDateDescIdDesc(1L, YearMonth.of(2025, 1)))
                    .willReturn(List.of(createTransaction(LocalDate.of(2025, 1, 15), 1000, "A")));

            List<AccountTransactionResponse> responses = accountTransactionService.getAll(1L, "2025-01");

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("잘못된 yearMonth 형식이면 IllegalArgumentException 발생")
        void getAllWithInvalidYearMonthThrowsException() {
            assertThatThrownBy(() -> accountTransactionService.getAll(null, "2025/01"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("YYYY-MM");
        }
    }

    @Nested
    @DisplayName("거래 내역 삭제 테스트")
    class DeleteTest {

        @Test
        @DisplayName("삭제 성공")
        void deleteSuccess() {
            AccountTransaction transaction = createTransaction(LocalDate.of(2025, 1, 15), 35000, "이마트 장보기");
            given(accountTransactionRepository.findById(1L)).willReturn(Optional.of(transaction));

            accountTransactionService.delete(1L);

            verify(accountTransactionRepository).delete(transaction);
        }

        @Test
        @DisplayName("존재하지 않는 거래 내역 삭제 시 예외 발생")
        void deleteNotFound() {
            given(accountTransactionRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> accountTransactionService.delete(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("존재하지 않는 거래 내역입니다. ID: 999");
        }
    }
}
