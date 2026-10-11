package com.honeybudget.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "계좌 등록 요청 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountCreateRequest {

    @Schema(description = "계좌 별칭/명칭 (예: 토스 생활비 통장, KB 비상금 통장)", example = "토스 생활비 통장")
    @NotBlank(message = "계좌 명칭은 필수입니다.")
    @Size(max = 50, message = "계좌 명칭은 50자 이하이어야 합니다.")
    private String accountName;

    @Schema(description = "금융기관/은행/증권사명 (예: 토스뱅크, 국민은행, 한국투자증권)", example = "토스뱅크")
    @NotBlank(message = "금융기관명은 필수입니다.")
    @Size(max = 30, message = "금융기관명은 30자 이하이어야 합니다.")
    private String institutionName;

    @Schema(description = "계좌 목적 카테고리(중분류) ID", example = "1")
    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    @Schema(description = "비고 및 메모", example = "매월 생활비 지출 통장")
    @Size(max = 200, message = "메모는 200자 이하이어야 합니다.")
    private String memo;
}
