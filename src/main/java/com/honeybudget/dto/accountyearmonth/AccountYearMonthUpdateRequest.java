package com.honeybudget.dto.accountyearmonth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/** 계좌 월별 금액 수정 요청 DTO. */
@Schema(description = "계좌 연간 월별 금액 수정 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountYearMonthUpdateRequest {

    @Schema(description = "수정할 월(1~12)별 금액. 포함된 월만 수정됨", example = "{\"3\": 700000}")
    @NotEmpty(message = "수정할 월별 금액은 필수입니다.")
    private Map<Integer, BigDecimal> amounts;
}
