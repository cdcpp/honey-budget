package com.honeybudget.dto.category;

import com.honeybudget.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "중분류 카테고리 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryResponse {

    @Schema(description = "카테고리 고유 ID", example = "1")
    private Long id;

    @Schema(description = "소속 대분류 그룹 ID", example = "1")
    private Long categoryGroupId;

    @Schema(description = "소속 대분류 그룹 코드", example = "100")
    private String categoryGroupCode;

    @Schema(description = "소속 대분류 그룹 명칭", example = "수입")
    private String categoryGroupName;

    @Schema(description = "중분류 카테고리 코드", example = "101")
    private String categoryCode;

    @Schema(description = "중분류 카테고리 명칭", example = "생활비 통장")
    private String categoryName;

    @Schema(description = "생성 일시", example = "2026-09-27T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정 일시", example = "2026-09-27T10:00:00")
    private LocalDateTime updatedAt;

    public static CategoryResponse from(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryGroupId(category.getGroup() != null ? category.getGroup().getId() : null)
                .categoryGroupCode(category.getGroup() != null ? category.getGroup().getCategoryGroupCode() : null)
                .categoryGroupName(category.getGroup() != null ? category.getGroup().getCategoryGroupName() : null)
                .categoryCode(category.getCategoryCode())
                .categoryName(category.getCategoryName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
