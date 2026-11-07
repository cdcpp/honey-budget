package com.honeybudget.dto.accounttransaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** 계좌 거래 내역 등록 요청 DTO. */
@Schema(description = "계좌 거래 내역 등록 요청 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountTransactionCreateRequest {

    @Schema(description = "거래가 발생한 계좌 ID", example = "1")
    @NotNull(message = "계좌 ID는 필수입니다.")
    private Long accountId;

    @Schema(description = "거래 일자 (yyyy-MM-dd). 월별 시트(yearMonth)는 거래 일자로부터 자동 계산됩니다.", example = "2025-01-15")
    @NotNull(message = "거래 일자는 필수입니다.")
    private LocalDate transactionDate;

    @Schema(description = "지출/거래 금액 (원 단위 정수)", example = "35000")
    @NotNull(message = "거래 금액은 필수입니다.")
    @Digits(integer = 15, fraction = 0, message = "거래 금액은 15자리 이하의 정수이어야 합니다.")
    private BigDecimal amount;

    @Schema(description = "사용처 / 내용 (예: 이마트 장보기, GS칼텍스 주유)", example = "이마트 장보기")
    @NotBlank(message = "사용처/내용은 필수입니다.")
    @Size(max = 100, message = "사용처/내용은 100자 이하이어야 합니다.")
    private String description;

    @Schema(description = "비고 / 메모", example = "주말 장보기")
    @Size(max = 255, message = "메모는 255자 이하이어야 합니다.")
    private String memo;
}
