package com.honeybudget.controller;

import com.honeybudget.domain.Account;
import com.honeybudget.domain.AccountYearMonth;
import com.honeybudget.domain.Category;
import com.honeybudget.domain.CategoryGroup;
import com.honeybudget.repository.AccountRepository;
import com.honeybudget.repository.AccountYearMonthRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountYearMonthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountYearMonthRepository accountYearMonthRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryGroupRepository categoryGroupRepository;

    private Account salaryAccount;
    private Account livingAccount;

    @BeforeEach
    void setUp() {
        accountYearMonthRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        categoryGroupRepository.deleteAllInBatch();

        CategoryGroup incomeGroup = categoryGroupRepository.save(
                CategoryGroup.builder().groupCode("100").groupName("수입").build()
        );
        CategoryGroup expenseGroup = categoryGroupRepository.save(
                CategoryGroup.builder().groupCode("200").groupName("지출").build()
        );

        Category incomeCat = categoryRepository.save(
                Category.builder().group(incomeGroup).categoryCode("101").categoryName("급여").build()
        );
        Category expenseCat = categoryRepository.save(
                Category.builder().group(expenseGroup).categoryCode("201").categoryName("생활비").build()
        );

        salaryAccount = accountRepository.save(
                Account.builder()
                        .accountName("급여 통장")
                        .institutionName("국민은행")
                        .category(incomeCat)
                        .build()
        );

        livingAccount = accountRepository.save(
                Account.builder()
                        .accountName("토스 생활비 통장")
                        .institutionName("토스뱅크")
                        .category(expenseCat)
                        .build()
        );
    }

    @Test
    @DisplayName("연도별 조회: GET /api/v1/account-year-months/year/{year} - 데이터 미존재 시 자동 초기화(0원) 생성 및 반환")
    void testGetByYearAutoInitialization() throws Exception {
        mockMvc.perform(get("/api/v1/account-year-months/year/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].year", is(2026)))
                .andExpect(jsonPath("$.data[0].categoryGroupName", anyOf(is("수입"), is("지출"))))
                .andExpect(jsonPath("$.data[0].amounts.1", is(0)))
                .andExpect(jsonPath("$.data[0].total", is(0)));
    }

    @Test
    @DisplayName("월별 금액 수정 및 연간 명세서 종합 조회 검증")
    void testUpdateAndAnnualStatement() throws Exception {
        // 1. 연도 데이터 초기화 조회
        mockMvc.perform(get("/api/v1/account-year-months/year/2026"))
                .andExpect(status().isOk());

        AccountYearMonth salaryAym = accountYearMonthRepository
                .findByAccountIdAndYear(salaryAccount.getId(), 2026).orElseThrow();
        AccountYearMonth livingAym = accountYearMonthRepository
                .findByAccountIdAndYear(livingAccount.getId(), 2026).orElseThrow();

        // 2. 급여통장 금액 수정 (1월: 3,000,000원, 2월: 3,000,000원)
        String salaryUpdateJson = """
                {
                    "amounts": {
                        "1": 3000000,
                        "2": 3000000
                    }
                }
                """;
        mockMvc.perform(put("/api/v1/account-year-months/" + salaryAym.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(salaryUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amounts.1", is(3000000)))
                .andExpect(jsonPath("$.data.total", is(6000000)));

        // 3. 생활비통장 금액 수정 (1월: 1,000,000원)
        String livingUpdateJson = """
                {
                    "amounts": {
                        "1": 1000000
                    }
                }
                """;
        mockMvc.perform(put("/api/v1/account-year-months/" + livingAym.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livingUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amounts.1", is(1000000)))
                .andExpect(jsonPath("$.data.total", is(1000000)));

        // 4. 연간 수입/지출/저축 명세표 종합 조회
        mockMvc.perform(get("/api/v1/account-year-months/year/2026/statement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.year", is(2026)))
                .andExpect(jsonPath("$.data.items", hasSize(2)))
                // 수입 그룹 소계 (1월: 3,000,000, 연간: 6,000,000)
                .andExpect(jsonPath("$.data.groupSummaries.수입.annualTotal", is(6000000)))
                .andExpect(jsonPath("$.data.groupSummaries.수입.monthlyTotals.1", is(3000000)))
                // 지출 그룹 소계 (1월: 1,000,000, 연간: 1,000,000)
                .andExpect(jsonPath("$.data.groupSummaries.지출.annualTotal", is(1000000)))
                .andExpect(jsonPath("$.data.groupSummaries.지출.monthlyTotals.1", is(1000000)))
                // 순수익 (1월: 3,000,000 - 1,000,000 = 2,000,000)
                .andExpect(jsonPath("$.data.monthlyNetIncome.1", is(2000000)))
                // 총 순수익 (6,000,000 - 1,000,000 = 5,000,000)
                .andExpect(jsonPath("$.data.totalNetIncome", is(5000000)));
    }
}
