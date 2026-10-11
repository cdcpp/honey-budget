package com.honeybudget.dto.account;

import com.honeybudget.domain.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "계좌 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountResponse {

    @Schema(description = "계좌 고유 ID", example = "1")
    private Long id;

    @Schema(description = "계좌 별칭/명칭", example = "토스 생활비 통장")
    private String accountName;

    @Schema(description = "금융기관/은행/증권사명", example = "토스뱅크")
    private String institutionName;

    @Schema(description = "소속 카테고리(중분류) ID", example = "1")
    private Long categoryId;

    @Schema(description = "소속 카테고리 코드", example = "101")
    private String categoryCode;

    @Schema(description = "소속 카테고리 명칭", example = "생활비 통장")
    private String categoryName;

    @Schema(description = "소속 대분류 그룹 ID", example = "1")
    private Long categoryGroupId;

    @Schema(description = "소속 대분류 그룹 명칭", example = "지출")
    private String categoryGroupName;

    @Schema(description = "계좌 활성 여부 (true: 사용중, false: 해지/비활성화)", example = "true")
    private Boolean useYn;

    @Schema(description = "비고 및 메모", example = "매월 생활비 지출 통장")
    private String memo;

    @Schema(description = "계좌 등록 일시", example = "2026-09-27T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정 일시", example = "2026-09-27T10:00:00")
    private LocalDateTime updatedAt;

    public static AccountResponse from(Account account) {
        if (account == null) {
            return null;
        }

        Long categoryId = null;
        String categoryCode = null;
        String categoryName = null;
        Long categoryGroupId = null;
        String categoryGroupName = null;

        if (account.getCategory() != null) {
            categoryId = account.getCategory().getId();
            categoryCode = account.getCategory().getCategoryCode();
            categoryName = account.getCategory().getCategoryName();
            if (account.getCategory().getGroup() != null) {
                categoryGroupId = account.getCategory().getGroup().getId();
                categoryGroupName = account.getCategory().getGroup().getCategoryGroupName();
            }
        }

        return AccountResponse.builder()
                .id(account.getId())
                .accountName(account.getAccountName())
                .institutionName(account.getInstitutionName())
                .categoryId(categoryId)
                .categoryCode(categoryCode)
                .categoryName(categoryName)
                .categoryGroupId(categoryGroupId)
                .categoryGroupName(categoryGroupName)
                .useYn(account.getUseYn())
                .memo(account.getMemo())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
