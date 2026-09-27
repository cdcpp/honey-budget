package com.honeybudget.service;

import com.honeybudget.domain.Account;
import com.honeybudget.domain.Category;
import com.honeybudget.domain.CategoryGroup;
import com.honeybudget.dto.account.AccountCreateRequest;
import com.honeybudget.dto.account.AccountResponse;
import com.honeybudget.dto.account.AccountUpdateRequest;
import com.honeybudget.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private AccountService accountService;

    private Category sampleCategory;
    private Category anotherCategory;

    @BeforeEach
    void setUp() {
        CategoryGroup group = CategoryGroup.builder()
                .groupCode("200")
                .groupName("지출")
                .build();

        sampleCategory = Category.builder()
                .group(group)
                .categoryCode("201")
                .categoryName("생활비 통장")
                .build();

        anotherCategory = Category.builder()
                .group(group)
                .categoryCode("202")
                .categoryName("비상금 통장")
                .build();
    }

    @Nested
    @DisplayName("계좌 등록 테스트")
    class CreateAccountTest {

        @Test
        @DisplayName("정상 등록 성공")
        void createAccountSuccess() {
            AccountCreateRequest request = AccountCreateRequest.builder()
                    .accountName("토스 생활비 통장")
                    .institutionName("토스뱅크")
                    .accountNumber("1000-1234-5678")
                    .balance(new BigDecimal("500000"))
                    .categoryId(1L)
                    .memo("생활비 전용")
                    .build();

            Account account = Account.builder()
                    .accountName("토스 생활비 통장")
                    .institutionName("토스뱅크")
                    .accountNumber("1000-1234-5678")
                    .balance(new BigDecimal("500000"))
                    .category(sampleCategory)
                    .memo("생활비 전용")
                    .build();

            given(accountRepository.existsByInstitutionNameAndAccountNumber("토스뱅크", "1000-1234-5678"))
                    .willReturn(false);
            given(categoryService.findCategoryById(1L)).willReturn(sampleCategory);
            given(accountRepository.save(any(Account.class))).willReturn(account);

            AccountResponse response = accountService.createAccount(request);

            assertThat(response).isNotNull();
            assertThat(response.getAccountName()).isEqualTo("토스 생활비 통장");
            assertThat(response.getInstitutionName()).isEqualTo("토스뱅크");
            assertThat(response.getAccountNumber()).isEqualTo("1000-1234-5678");
            assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("500000"));
            assertThat(response.getCategoryName()).isEqualTo("생활비 통장");
            assertThat(response.getUseYn()).isTrue();
            verify(accountRepository).save(any(Account.class));
        }

        @Test
        @DisplayName("동일 금융기관 및 계좌번호 중복 시 IllegalArgumentException 발생")
        void createDuplicateThrowsException() {
            AccountCreateRequest request = AccountCreateRequest.builder()
                    .accountName("토스 생활비 통장")
                    .institutionName("토스뱅크")
                    .accountNumber("1000-1234-5678")
                    .categoryId(1L)
                    .build();

            given(accountRepository.existsByInstitutionNameAndAccountNumber("토스뱅크", "1000-1234-5678"))
                    .willReturn(true);

            assertThatThrownBy(() -> accountService.createAccount(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("해당 금융기관에 이미 등록된 계좌번호입니다: 토스뱅크 - 1000-1234-5678");
        }
    }

    @Nested
    @DisplayName("계좌 수정 테스트")
    class UpdateAccountTest {

        @Test
        @DisplayName("계좌 정보 및 카테고리 수정 성공")
        void updateAccountSuccess() {
            Account account = Account.builder()
                    .accountName("토스 생활비 통장")
                    .institutionName("토스뱅크")
                    .accountNumber("1000-1234-5678")
                    .balance(new BigDecimal("500000"))
                    .category(sampleCategory)
                    .memo("생활비 전용")
                    .build();

            AccountUpdateRequest updateRequest = AccountUpdateRequest.builder()
                    .accountName("토스 비상금 통장")
                    .categoryId(2L)
                    .memo("비상금으로 전환")
                    .build();

            given(accountRepository.findById(1L)).willReturn(Optional.of(account));
            given(categoryService.findCategoryById(2L)).willReturn(anotherCategory);

            AccountResponse response = accountService.updateAccount(1L, updateRequest);

            assertThat(response.getAccountName()).isEqualTo("토스 비상금 통장");
            assertThat(response.getMemo()).isEqualTo("비상금으로 전환");
            assertThat(account.getCategory()).isEqualTo(anotherCategory);
        }

        @Test
        @DisplayName("존재하지 않는 계좌 ID 수정 시 IllegalArgumentException 발생")
        void updateNonExistentAccountThrowsException() {
            AccountUpdateRequest updateRequest = AccountUpdateRequest.builder()
                    .accountName("수정 명칭")
                    .build();

            given(accountRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.updateAccount(999L, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("존재하지 않는 계좌입니다. ID: 999");
        }
    }

    @Nested
    @DisplayName("계좌 조회 테스트")
    class GetAccountTest {

        @Test
        @DisplayName("단건 조회 성공")
        void getAccountByIdSuccess() {
            Account account = Account.builder()
                    .accountName("토스 생활비 통장")
                    .institutionName("토스뱅크")
                    .accountNumber("1000-1234-5678")
                    .category(sampleCategory)
                    .build();

            given(accountRepository.findById(1L)).willReturn(Optional.of(account));

            AccountResponse response = accountService.getAccountById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getAccountName()).isEqualTo("토스 생활비 통장");
        }

        @Test
        @DisplayName("필터 없이 전체 조회")
        void getAllAccountsWithoutFilter() {
            Account acc1 = Account.builder().accountName("계좌1").institutionName("국민").accountNumber("111").category(sampleCategory).build();
            Account acc2 = Account.builder().accountName("계좌2").institutionName("신한").accountNumber("222").category(sampleCategory).build();

            given(accountRepository.findAllByOrderByIdDesc()).willReturn(List.of(acc1, acc2));

            List<AccountResponse> responses = accountService.getAllAccounts(null, null);

            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("활성 여부(useYn) 필터로 조회")
        void getAllAccountsWithUseYnFilter() {
            Account acc1 = Account.builder().accountName("계좌1").institutionName("국민").accountNumber("111").category(sampleCategory).build();

            given(accountRepository.findAllByUseYnOrderByIdDesc(true)).willReturn(List.of(acc1));

            List<AccountResponse> responses = accountService.getAllAccounts(true, null);

            assertThat(responses).hasSize(1);
        }
    }

    @Nested
    @DisplayName("계좌 삭제(Soft Delete) 테스트")
    class DeleteAccountTest {

        @Test
        @DisplayName("삭제 시 계좌 useYn이 false로 변경됨")
        void deleteAccountSuccess() {
            Account account = Account.builder()
                    .accountName("토스 생활비 통장")
                    .institutionName("토스뱅크")
                    .accountNumber("1000-1234-5678")
                    .category(sampleCategory)
                    .build();

            given(accountRepository.findById(1L)).willReturn(Optional.of(account));

            assertThat(account.getUseYn()).isTrue();

            accountService.deleteAccount(1L);

            assertThat(account.getUseYn()).isFalse();
        }
    }

    @Nested
    @DisplayName("계좌 입출금 테스트")
    class DepositAndWithdrawTest {

        @Test
        @DisplayName("입금 성공")
        void depositSuccess() {
            Account account = Account.builder()
                    .accountName("토스 생활비 통장")
                    .institutionName("토스뱅크")
                    .accountNumber("1000-1234-5678")
                    .balance(new BigDecimal("100000"))
                    .category(sampleCategory)
                    .build();

            given(accountRepository.findById(1L)).willReturn(Optional.of(account));

            AccountResponse response = accountService.deposit(1L, new BigDecimal("50000"));

            assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("150000"));
        }

        @Test
        @DisplayName("출금 성공")
        void withdrawSuccess() {
            Account account = Account.builder()
                    .accountName("토스 생활비 통장")
                    .institutionName("토스뱅크")
                    .accountNumber("1000-1234-5678")
                    .balance(new BigDecimal("100000"))
                    .category(sampleCategory)
                    .build();

            given(accountRepository.findById(1L)).willReturn(Optional.of(account));

            AccountResponse response = accountService.withdraw(1L, new BigDecimal("40000"));

            assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("60000"));
        }
    }
}
