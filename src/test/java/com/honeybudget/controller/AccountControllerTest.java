package com.honeybudget.controller;

import com.honeybudget.domain.Account;
import com.honeybudget.domain.Category;
import com.honeybudget.domain.CategoryGroup;
import com.honeybudget.repository.AccountRepository;
import com.honeybudget.repository.CategoryGroupRepository;
import com.honeybudget.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryGroupRepository categoryGroupRepository;

    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        categoryGroupRepository.deleteAllInBatch();

        CategoryGroup group = categoryGroupRepository.save(
                CategoryGroup.builder().groupCode("200").groupName("지출").build()
        );

        sampleCategory = categoryRepository.save(
                Category.builder().group(group).categoryCode("201").categoryName("생활비 통장").build()
        );
    }

    @Test
    @DisplayName("계좌 등록 성공: POST /api/v1/accounts")
    void testCreateAccountSuccess() throws Exception {
        String requestJson = String.format("""
                {
                    "accountName": "토스 생활비 통장",
                    "institutionName": "토스뱅크",
                    "accountNumber": "1000-1234-5678",
                    "balance": 500000,
                    "categoryId": %d,
                    "memo": "생활비 전용"
                }
                """, sampleCategory.getId());

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accountName", is("토스 생활비 통장")))
                .andExpect(jsonPath("$.data.institutionName", is("토스뱅크")))
                .andExpect(jsonPath("$.data.accountNumber", is("1000-1234-5678")))
                .andExpect(jsonPath("$.data.balance", is(500000)))
                .andExpect(jsonPath("$.data.categoryCode", is("201")))
                .andExpect(jsonPath("$.data.useYn", is(true)))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    @DisplayName("계좌 등록 실패: 동일 금융기관 및 계좌번호 중복 시 400 Bad Request 반환")
    void testCreateDuplicateAccountThrowsBadRequest() throws Exception {
        String requestJson = String.format("""
                {
                    "accountName": "토스 생활비 통장",
                    "institutionName": "토스뱅크",
                    "accountNumber": "1000-1234-5678",
                    "balance": 500000,
                    "categoryId": %d
                }
                """, sampleCategory.getId());

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message").value("해당 금융기관에 이미 등록된 계좌번호입니다: 토스뱅크 - 1000-1234-5678"));
    }

    @Test
    @DisplayName("계좌 정보 수정 성공: PUT /api/v1/accounts/{id}")
    void testUpdateAccountSuccess() throws Exception {
        Account account = accountRepository.save(
                Account.builder()
                        .accountName("토스 생활비 통장")
                        .institutionName("토스뱅크")
                        .accountNumber("1000-1234-5678")
                        .balance(new BigDecimal("500000"))
                        .category(sampleCategory)
                        .memo("초기 메모")
                        .build()
        );

        String updateJson = """
                {
                    "accountName": "토스 비상금 통장",
                    "memo": "비상금 통장으로 변경"
                }
                """;

        mockMvc.perform(put("/api/v1/accounts/" + account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accountName", is("토스 비상금 통장")))
                .andExpect(jsonPath("$.data.memo", is("비상금 통장으로 변경")));
    }

    @Test
    @DisplayName("계좌 단건 조회: GET /api/v1/accounts/{id}")
    void testGetAccountByIdSuccess() throws Exception {
        Account account = accountRepository.save(
                Account.builder()
                        .accountName("토스 생활비 통장")
                        .institutionName("토스뱅크")
                        .accountNumber("1000-1234-5678")
                        .balance(new BigDecimal("500000"))
                        .category(sampleCategory)
                        .build()
        );

        mockMvc.perform(get("/api/v1/accounts/" + account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accountName", is("토스 생활비 통장")))
                .andExpect(jsonPath("$.data.institutionName", is("토스뱅크")));
    }

    @Test
    @DisplayName("계좌 목록 조회 및 필터링: GET /api/v1/accounts")
    void testGetAllAccountsAndFilter() throws Exception {
        Account account1 = accountRepository.save(
                Account.builder()
                        .accountName("생활비 통장")
                        .institutionName("국민은행")
                        .accountNumber("111-222-333")
                        .balance(new BigDecimal("100000"))
                        .category(sampleCategory)
                        .build()
        );

        Account account2 = accountRepository.save(
                Account.builder()
                        .accountName("해지 통장")
                        .institutionName("신한은행")
                        .accountNumber("444-555-666")
                        .balance(new BigDecimal("0"))
                        .category(sampleCategory)
                        .build()
        );
        account2.deactivate();
        accountRepository.save(account2);

        // 전체 조회 -> 2건
        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        // 활성 계좌만 조회 (useYn=true) -> 1건
        mockMvc.perform(get("/api/v1/accounts").param("useYn", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].accountName", is("생활비 통장")));
    }

    @Test
    @DisplayName("계좌 비활성화(Soft Delete): DELETE /api/v1/accounts/{id}")
    void testDeleteAccountSuccess() throws Exception {
        Account account = accountRepository.save(
                Account.builder()
                        .accountName("토스 생활비 통장")
                        .institutionName("토스뱅크")
                        .accountNumber("1000-1234-5678")
                        .balance(new BigDecimal("500000"))
                        .category(sampleCategory)
                        .build()
        );

        mockMvc.perform(delete("/api/v1/accounts/" + account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message").value("계좌가 성공적으로 비활성화(해지)되었습니다."));

        mockMvc.perform(get("/api/v1/accounts/" + account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.useYn", is(false)));
    }

    @Test
    @DisplayName("계좌 입금 및 출금: POST /api/v1/accounts/{id}/deposit, /withdraw")
    void testDepositAndWithdraw() throws Exception {
        Account account = accountRepository.save(
                Account.builder()
                        .accountName("토스 생활비 통장")
                        .institutionName("토스뱅크")
                        .accountNumber("1000-1234-5678")
                        .balance(new BigDecimal("100000.00"))
                        .category(sampleCategory)
                        .build()
        );

        // 50,000원 입금 -> 잔액 150,000
        String depositJson = """
                {
                    "amount": 50000
                }
                """;
        mockMvc.perform(post("/api/v1/accounts/" + account.getId() + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance", is(150000.0)));

        // 30,000원 출금 -> 잔액 120,000
        String withdrawJson = """
                {
                    "amount": 30000
                }
                """;
        mockMvc.perform(post("/api/v1/accounts/" + account.getId() + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(withdrawJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance", is(120000.0)));

        // 잔액 초과 출금(200,000원) 시 400 Bad Request
        String overWithdrawJson = """
                {
                    "amount": 200000
                }
                """;
        mockMvc.perform(post("/api/v1/accounts/" + account.getId() + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(overWithdrawJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("잔액이 부족합니다")));
    }
}
