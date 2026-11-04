package com.honeybudget.dto.accounttransaction;

import com.honeybudget.domain.AccountTransaction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "계좌 거래 내역 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountTransactionResponse {

    @Schema(description = "거래 내역 고유 ID", example = "1")
    private Long id;

    @Schema(description = "계좌 ID", example = "1")
    private Long accountId;

    @Schema(description = "계좌 별칭/명칭", example = "토스 생활비 통장")
    private String accountName;

    @Schema(description = "금융기관명", example = "토스뱅크")
    private String institutionName;

    @Schema(description = "월별 시트 기준 연월 (YYYY-MM)", example = "2025-01")
    private String yearMonth;

    @Schema(description = "거래 일자", example = "2025-01-15")
    private LocalDate transactionDate;

    @Schema(description = "거래 금액", example = "35000")
    private BigDecimal amount;

    @Schema(description = "사용처 / 내용", example = "이마트 장보기")
    private String description;

    @Schema(description = "비고 / 메모", example = "주말 장보기")
    private String memo;

    @Schema(description = "등록 일시", example = "2026-09-27T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정 일시", example = "2026-09-27T10:00:00")
    private LocalDateTime updatedAt;

    public static AccountTransactionResponse from(AccountTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        Long accountId = null;
        String accountName = null;
        String institutionName = null;
        if (transaction.getAccount() != null) {
            accountId = transaction.getAccount().getId();
            accountName = transaction.getAccount().getAccountName();
            institutionName = transaction.getAccount().getInstitutionName();
        }

        return AccountTransactionResponse.builder()
                .id(transaction.getId())
                .accountId(accountId)
                .accountName(accountName)
                .institutionName(institutionName)
                .yearMonth(transaction.getYearMonth() != null ? transaction.getYearMonth().toString() : null)
                .transactionDate(transaction.getTransactionDate())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .memo(transaction.getMemo())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
