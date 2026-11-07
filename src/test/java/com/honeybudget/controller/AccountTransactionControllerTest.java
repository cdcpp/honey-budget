package com.honeybudget.controller;

import com.honeybudget.domain.Account;
import com.honeybudget.domain.AccountTransaction;
import com.honeybudget.domain.Category;
import com.honeybudget.domain.CategoryGroup;
import com.honeybudget.repository.AccountRepository;
import com.honeybudget.repository.AccountTransactionRepository;
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
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 계좌 거래 내역 API 컨트롤러 통합 테스트.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryGroupRepository categoryGroupRepository;

    private Account sampleAccount;

    @BeforeEach
    void setUp() {
        accountTransactionRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        categoryGroupRepository.deleteAllInBatch();

        CategoryGroup group = categoryGroupRepository.save(
                CategoryGroup.builder().groupCode("200").groupName("지출").build()
        );
        Category category = categoryRepository.save(
                Category.builder().group(group).categoryCode("201").categoryName("생활비 통장").build()
        );
        sampleAccount = accountRepository.save(
                Account.builder().accountName("토스 생활비 통장").institutionName("토스뱅크").category(category).build()
        );
    }

    private AccountTransaction saveTransaction(LocalDate date, long amount, String description) {
        return accountTransactionRepository.save(
                AccountTransaction.builder()
                        .account(sampleAccount)
                        .transactionDate(date)
                        .amount(BigDecimal.valueOf(amount))
                        .description(description)
                        .build()
        );
    }

    @Test
    @DisplayName("거래 내역 등록 성공: POST /api/v1/account-transactions")
    void testCreateSuccess() throws Exception {
        String requestJson = String.format("""
                {
                    "accountId": %d,
                    "transactionDate": "2025-01-15",
                    "amount": 35000,
                    "description": "이마트 장보기",
                    "memo": "주말 장보기"
                }
                """, sampleAccount.getId());

        mockMvc.perform(post("/api/v1/account-transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.accountId").value(sampleAccount.getId()))
                .andExpect(jsonPath("$.data.yearMonth", is("2025-01")))
                .andExpect(jsonPath("$.data.transactionDate", is("2025-01-15")))
                .andExpect(jsonPath("$.data.amount").value(35000))
                .andExpect(jsonPath("$.data.description", is("이마트 장보기")));
    }

    @Test
    @DisplayName("거래 내역 등록 실패 - 필수값 누락 시 400")
    void testCreateValidationFail() throws Exception {
        String requestJson = String.format("""
                {
                    "accountId": %d,
                    "amount": 35000
                }
                """, sampleAccount.getId());

        mockMvc.perform(post("/api/v1/account-transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("거래 내역 수정 성공: PUT /api/v1/account-transactions/{id}")
    void testUpdateSuccess() throws Exception {
        AccountTransaction transaction = saveTransaction(LocalDate.of(2025, 1, 15), 35000, "이마트 장보기");

        String updateJson = """
                {
                    "transactionDate": "2025-02-03",
                    "amount": 42000,
                    "description": "GS칼텍스 주유",
                    "memo": "출장 주유"
                }
                """;

        mockMvc.perform(put("/api/v1/account-transactions/" + transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.yearMonth", is("2025-02")))
                .andExpect(jsonPath("$.data.amount").value(42000))
                .andExpect(jsonPath("$.data.description", is("GS칼텍스 주유")))
                .andExpect(jsonPath("$.data.memo", is("출장 주유")));
    }

    @Test
    @DisplayName("거래 내역 단건 조회: GET /api/v1/account-transactions/{id}")
    void testGetByIdSuccess() throws Exception {
        AccountTransaction transaction = saveTransaction(LocalDate.of(2025, 1, 15), 35000, "이마트 장보기");

        mockMvc.perform(get("/api/v1/account-transactions/" + transaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.description", is("이마트 장보기")))
                .andExpect(jsonPath("$.data.accountName", is("토스 생활비 통장")));
    }

    @Test
    @DisplayName("거래 내역 목록 조회 및 필터링: GET /api/v1/account-transactions")
    void testGetAllAndFilter() throws Exception {
        saveTransaction(LocalDate.of(2025, 1, 15), 35000, "이마트 장보기");
        saveTransaction(LocalDate.of(2025, 1, 20), 50000, "GS칼텍스 주유");
        saveTransaction(LocalDate.of(2025, 2, 1), 12000, "외식");

        // 전체 조회 -> 3건 (거래일자 내림차순)
        mockMvc.perform(get("/api/v1/account-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].description", is("외식")));

        // 월 필터 -> 2건
        mockMvc.perform(get("/api/v1/account-transactions").param("yearMonth", "2025-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));

        // 계좌 + 월 필터 -> 1건
        mockMvc.perform(get("/api/v1/account-transactions")
                        .param("accountId", String.valueOf(sampleAccount.getId()))
                        .param("yearMonth", "2025-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        // 잘못된 월 형식 -> 400
        mockMvc.perform(get("/api/v1/account-transactions").param("yearMonth", "2025/01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("거래 내역 삭제: DELETE /api/v1/account-transactions/{id}")
    void testDeleteSuccess() throws Exception {
        AccountTransaction transaction = saveTransaction(LocalDate.of(2025, 1, 15), 35000, "이마트 장보기");

        mockMvc.perform(delete("/api/v1/account-transactions/" + transaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message").value("거래 내역이 성공적으로 삭제되었습니다."));

        mockMvc.perform(get("/api/v1/account-transactions/" + transaction.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }
}
