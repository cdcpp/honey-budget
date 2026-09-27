package com.honeybudget.dto.categorygroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "대분류 그룹 등록 요청 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryGroupCreateRequest {

    @Schema(description = "대분류 그룹 코드 (예: 100, 200)", example = "100")
    @NotBlank(message = "대분류 그룹 코드는 필수입니다.")
    @Size(max = 10, message = "대분류 그룹 코드는 10자 이하이어야 합니다.")
    private String categoryGroupCode;

    @Schema(description = "대분류 그룹 명칭 (예: 수입, 지출)", example = "수입")
    @NotBlank(message = "대분류 그룹 명칭은 필수입니다.")
    @Size(max = 20, message = "대분류 그룹 명칭은 20자 이하이어야 합니다.")
    private String categoryGroupName;
}
