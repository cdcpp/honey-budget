package com.honeybudget.dto.accountyearmonth;

import com.honeybudget.domain.Account;
import com.honeybudget.domain.AccountYearMonth;
import com.honeybudget.domain.Category;
import com.honeybudget.domain.CategoryGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Schema(description = "계좌 연간 월별 금액 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountYearMonthResponse {

    @Schema(description = "고유 ID", example = "1")
    private Long id;

    @Schema(description = "계좌 ID", example = "1")
    private Long accountId;

    @Schema(description = "계좌 명칭", example = "토스 생활비 통장")
    private String accountName;

    @Schema(description = "금융기관/은행명", example = "토스뱅크")
    private String institutionName;

    @Schema(description = "소속 중분류 카테고리 ID", example = "1")
    private Long categoryId;

    @Schema(description = "소속 중분류 카테고리 코드", example = "101")
    private String categoryCode;

    @Schema(description = "소속 중분류 카테고리 명칭", example = "생활비")
    private String categoryName;

    @Schema(description = "소속 대분류 그룹 ID", example = "1")
    private Long categoryGroupId;

    @Schema(description = "소속 대분류 그룹 명칭 (수입, 지출, 저축 등)", example = "지출")
    private String categoryGroupName;

    @Schema(description = "연도", example = "2026")
    private Integer year;

    @Schema(description = "월(1~12)별 금액")
    private Map<Integer, BigDecimal> amounts;

    @Schema(description = "연간 합계")
    private BigDecimal total;

    @Schema(description = "등록 일시")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정 일시")
    private LocalDateTime updatedAt;

    public static AccountYearMonthResponse from(AccountYearMonth entity) {
        if (entity == null) {
            return null;
        }

        Account account = entity.getAccount();
        String institutionName = null;
        Long categoryId = null;
        String categoryCode = null;
        String categoryName = null;
        Long categoryGroupId = null;
        String categoryGroupName = null;

        if (account != null) {
            institutionName = account.getInstitutionName();
            Category category = account.getCategory();
            if (category != null) {
                categoryId = category.getId();
                categoryCode = category.getCategoryCode();
                categoryName = category.getCategoryName();
                CategoryGroup group = category.getGroup();
                if (group != null) {
                    categoryGroupId = group.getId();
                    categoryGroupName = group.getCategoryGroupName();
                }
            }
        }

        Map<Integer, BigDecimal> amounts = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (int m = 1; m <= 12; m++) {
            BigDecimal v = entity.getAmount(m);
            amounts.put(m, v);
            total = total.add(v);
        }

        return AccountYearMonthResponse.builder()
                .id(entity.getId())
                .accountId(account != null ? account.getId() : null)
                .accountName(account != null ? account.getAccountName() : null)
                .institutionName(institutionName)
                .categoryId(categoryId)
                .categoryCode(categoryCode)
                .categoryName(categoryName)
                .categoryGroupId(categoryGroupId)
                .categoryGroupName(categoryGroupName)
                .year(entity.getYear())
                .amounts(amounts)
                .total(total)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
