package com.honeybudget.dto.accountyearmonth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "연간 수입/저축/지출 명세표 종합 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AnnualStatementResponse {

    @Schema(description = "조회 연도", example = "2026")
    private Integer year;

    @Schema(description = "계좌별 연간 월별 데이터 목록")
    private List<AccountYearMonthResponse> items;

    @Schema(description = "대분류 그룹별(수입, 저축, 지출) 월별 소계 및 연간 총계")
    private Map<String, GroupSummary> groupSummaries;

    @Schema(description = "월별 순수익 (수입 - 저축 - 지출)")
    private Map<Integer, BigDecimal> monthlyNetIncome;

    @Schema(description = "연간 총 순수익")
    private BigDecimal totalNetIncome;

    @Schema(description = "대분류 그룹별 소계 DTO")
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class GroupSummary {
        @Schema(description = "대분류 그룹 명칭 (수입, 저축, 지출 등)", example = "수입")
        private String groupName;

        @Schema(description = "월(1~12)별 소계")
        private Map<Integer, BigDecimal> monthlyTotals;

        @Schema(description = "연간 총 소계")
        private BigDecimal annualTotal;
    }

    public static AnnualStatementResponse of(Integer year, List<AccountYearMonthResponse> items) {
        Map<String, Map<Integer, BigDecimal>> groupMonthlyMap = new LinkedHashMap<>();
        Map<String, BigDecimal> groupAnnualMap = new LinkedHashMap<>();

        Map<Integer, BigDecimal> netIncomeMonthly = new LinkedHashMap<>();
        for (int m = 1; m <= 12; m++) {
            netIncomeMonthly.put(m, BigDecimal.ZERO);
        }

        for (AccountYearMonthResponse item : items) {
            String groupName = item.getCategoryGroupName() != null ? item.getCategoryGroupName() : "기타";

            groupMonthlyMap.putIfAbsent(groupName, new LinkedHashMap<>());
            Map<Integer, BigDecimal> monthlyMap = groupMonthlyMap.get(groupName);

            for (int m = 1; m <= 12; m++) {
                monthlyMap.putIfAbsent(m, BigDecimal.ZERO);
                BigDecimal amount = item.getAmounts().getOrDefault(m, BigDecimal.ZERO);
                monthlyMap.put(m, monthlyMap.get(m).add(amount));

                // 수입은 더하고, 저축 및 지출은 차감
                BigDecimal currentNet = netIncomeMonthly.get(m);
                if ("수입".equals(groupName)) {
                    netIncomeMonthly.put(m, currentNet.add(amount));
                } else if ("지출".equals(groupName) || "저축".equals(groupName)) {
                    netIncomeMonthly.put(m, currentNet.subtract(amount));
                }
            }

            BigDecimal currentAnnual = groupAnnualMap.getOrDefault(groupName, BigDecimal.ZERO);
            groupAnnualMap.put(groupName, currentAnnual.add(item.getTotal()));
        }

        Map<String, GroupSummary> summaries = new LinkedHashMap<>();
        for (Map.Entry<String, Map<Integer, BigDecimal>> entry : groupMonthlyMap.entrySet()) {
            String gName = entry.getKey();
            summaries.put(gName, GroupSummary.builder()
                    .groupName(gName)
                    .monthlyTotals(entry.getValue())
                    .annualTotal(groupAnnualMap.getOrDefault(gName, BigDecimal.ZERO))
                    .build());
        }

        BigDecimal totalNet = netIncomeMonthly.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AnnualStatementResponse.builder()
                .year(year)
                .items(items)
                .groupSummaries(summaries)
                .monthlyNetIncome(netIncomeMonthly)
                .totalNetIncome(totalNet)
                .build();
    }
}
