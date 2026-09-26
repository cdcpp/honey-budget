package com.honeybudget.dto.asset;

import com.honeybudget.domain.Asset;
import com.honeybudget.domain.AssetRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "자산(통장/계좌) 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AssetResponse {

    @Schema(description = "자산 고유 ID", example = "1")
    private Long id;

    @Schema(description = "자산 이름 (통장명)", example = "생활비통장")
    private String name;

    @Schema(description = "자산 역할/용도 코드", example = "EXPENSE")
    private AssetRole role;

    @Schema(description = "자산 역할/용도 한글 설명", example = "지출용")
    private String roleDescription;

    @Schema(description = "생성 일시", example = "2026-09-26T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정 일시", example = "2026-09-26T14:30:00")
    private LocalDateTime updatedAt;

    public static AssetResponse from(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .name(asset.getName())
                .role(asset.getRole())
                .roleDescription(asset.getRole().getDescription())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
