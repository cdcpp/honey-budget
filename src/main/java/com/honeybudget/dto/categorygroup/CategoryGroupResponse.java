package com.honeybudget.dto.categorygroup;

import com.honeybudget.domain.CategoryGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "대분류 그룹 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryGroupResponse {

    @Schema(description = "대분류 그룹 고유 ID", example = "1")
    private Long id;

    @Schema(description = "대분류 그룹 코드", example = "100")
    private String categoryGroupCode;

    @Schema(description = "대분류 그룹 명칭", example = "수입")
    private String categoryGroupName;

    @Schema(description = "생성 일시", example = "2026-09-27T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정 일시", example = "2026-09-27T10:00:00")
    private LocalDateTime updatedAt;

    public static CategoryGroupResponse from(CategoryGroup group) {
        if (group == null) {
            return null;
        }
        return CategoryGroupResponse.builder()
                .id(group.getId())
                .categoryGroupCode(group.getCategoryGroupCode())
                .categoryGroupName(group.getCategoryGroupName())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}
