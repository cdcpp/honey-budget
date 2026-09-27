package com.honeybudget.dto.categorygroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "대분류 그룹 수정 요청 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryGroupUpdateRequest {

    @Schema(description = "수정할 대분류 그룹 명칭 (예: 수입 (급여/상여))", example = "수입 (급여/상여)")
    @NotBlank(message = "대분류 그룹 명칭은 필수입니다.")
    @Size(max = 20, message = "대분류 그룹 명칭은 20자 이하이어야 합니다.")
    private String categoryGroupName;
}
