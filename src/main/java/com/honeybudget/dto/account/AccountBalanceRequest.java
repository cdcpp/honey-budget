package com.honeybudget.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "계좌 잔액 입출금 요청 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountBalanceRequest {

    @Schema(description = "입출금 금액 (0보다 커야 함)", example = "50000")
    @NotNull(message = "금액은 필수입니다.")
    @DecimalMin(value = "0.01", message = "금액은 0보다 커야 합니다.")
    private BigDecimal amount;
}
