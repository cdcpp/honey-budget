package com.honeybudget.dto.accountyearmonth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "계좌 연간 월별 금액 등록 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountYearMonthCreateRequest {

    @Schema(description = "계좌 ID", example = "1")
    @NotNull(message = "계좌 ID는 필수입니다.")
    private Long accountId;

    @Schema(description = "연도", example = "2026")
    @NotNull(message = "연도는 필수입니다.")
    private Integer year;

    @Schema(description = "월(1~12)별 금액. 미입력 월은 0", example = "{\"1\": 500000, \"2\": 300000}")
    private Map<Integer, BigDecimal> amounts;
}
