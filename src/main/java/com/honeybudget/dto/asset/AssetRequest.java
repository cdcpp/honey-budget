package com.honeybudget.dto.asset;

import com.honeybudget.domain.AssetRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "자산(통장/계좌) 등록 요청 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AssetRequest {

    @Schema(description = "자산 이름 (통장명)", example = "생활비통장")
    @NotBlank(message = "자산 이름은 필수입니다.")
    @Size(max = 50, message = "자산 이름은 50자 이하로 입력해주세요.")
    private String name;

    @Schema(description = "자산 역할/용도 (EXPENSE: 지출용, INVESTMENT: 투자용, SAVINGS: 저축용, RESERVE: 비상금용)", example = "EXPENSE")
    @NotNull(message = "자산 역할은 필수입니다.")
    private AssetRole role;
}
