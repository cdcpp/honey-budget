package com.honeybudget.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "중분류 카테고리 수정 요청 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryUpdateRequest {

    @Schema(description = "변경할 대분류 그룹 ID (변경하지 않을 경우 null 가능)", example = "1")
    private Long categoryGroupId;

    @Schema(description = "수정할 중분류 카테고리 명칭", example = "식비 통장")
    @NotBlank(message = "중분류 카테고리 명칭은 필수입니다.")
    @Size(max = 20, message = "중분류 카테고리 명칭은 20자 이하이어야 합니다.")
    private String categoryName;
}
