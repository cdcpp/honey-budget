package com.honeybudget.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "중분류 카테고리 등록 요청 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryCreateRequest {

    @Schema(description = "소속될 대분류 그룹 ID", example = "1")
    @NotNull(message = "대분류 그룹 ID는 필수입니다.")
    private Long categoryGroupId;

    @Schema(description = "중분류 카테고리 코드 (예: 101, 201)", example = "101")
    @NotBlank(message = "중분류 카테고리 코드는 필수입니다.")
    @Size(max = 10, message = "중분류 카테고리 코드는 10자 이하이어야 합니다.")
    private String categoryCode;

    @Schema(description = "중분류 카테고리 명칭 (예: 생활비 통장)", example = "생활비 통장")
    @NotBlank(message = "중분류 카테고리 명칭은 필수입니다.")
    @Size(max = 20, message = "중분류 카테고리 명칭은 20자 이하이어야 합니다.")
    private String categoryName;
}
