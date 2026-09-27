package com.honeybudget.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "계좌 정보 수정 요청 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountUpdateRequest {

    @Schema(description = "수정할 계좌 별칭/명칭", example = "토스 비상금 통장")
    @NotBlank(message = "계좌 명칭은 필수입니다.")
    @Size(max = 50, message = "계좌 명칭은 50자 이하이어야 합니다.")
    private String accountName;

    @Schema(description = "변경할 목적 카테고리(중분류) ID (변경하지 않을 경우 null 가능)", example = "2")
    private Long categoryId;

    @Schema(description = "수정할 비고 및 메모", example = "용도 변경: 생활비 -> 비상금")
    @Size(max = 200, message = "메모는 200자 이하이어야 합니다.")
    private String memo;
}
