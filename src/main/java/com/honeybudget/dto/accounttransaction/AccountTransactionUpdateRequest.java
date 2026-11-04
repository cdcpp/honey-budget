package com.honeybudget.dto.accounttransaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "계좌 거래 내역 수정 요청 DTO (null인 항목은 기존 값 유지, memo는 요청 값으로 덮어씀)")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountTransactionUpdateRequest {

    @Schema(description = "수정할 거래 일자 (변경 시 yearMonth도 자동 변경)", example = "2025-02-03")
    private LocalDate transactionDate;

    @Schema(description = "수정할 거래 금액", example = "42000")
    @Digits(integer = 15, fraction = 0, message = "거래 금액은 15자리 이하의 정수이어야 합니다.")
    private BigDecimal amount;

    @Schema(description = "수정할 사용처 / 내용", example = "GS칼텍스 주유")
    @Size(max = 100, message = "사용처/내용은 100자 이하이어야 합니다.")
    private String description;

    @Schema(description = "수정할 비고 / 메모 (null 전달 시 메모 삭제)", example = "출장 주유")
    @Size(max = 255, message = "메모는 255자 이하이어야 합니다.")
    private String memo;
}
