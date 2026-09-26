package com.honeybudget.controller;

import com.honeybudget.common.ApiResponse;
import com.honeybudget.dto.asset.AssetRequest;
import com.honeybudget.dto.asset.AssetResponse;
import com.honeybudget.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Asset (자산/통장)", description = "자산 및 통장 계좌 관리 API")
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    /**
     * 신규 자산(통장/계좌) 등록
     */
    @Operation(summary = "신규 자산 등록", description = "새로운 자산(통장/계좌)을 생성합니다. 자산 이름은 중복될 수 없습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "자산 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 자산 이름 중복")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<AssetResponse>> createAsset(
            @Valid @RequestBody AssetRequest request
    ) {
        AssetResponse response = assetService.createAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("자산이 성공적으로 등록되었습니다.", response));
    }

    /**
     * 등록된 전체 자산 목록 조회
     */
    @Operation(summary = "전체 자산 목록 조회", description = "등록된 모든 자산(통장/계좌)의 목록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "자산 목록 조회 성공")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AssetResponse>>> getAllAssets() {
        List<AssetResponse> assets = assetService.getAllAssets();
        return ResponseEntity.ok(ApiResponse.success(assets));
    }

    /**
     * 자산 단건 조회
     */
    @Operation(summary = "자산 단건 상세 조회", description = "자산 ID를 기반으로 특정 자산의 상세 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "자산 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "존재하지 않는 자산 ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetResponse>> getAssetById(
            @Parameter(description = "조회할 자산의 고유 ID", example = "1")
            @PathVariable("id") Long id
    ) {
        AssetResponse asset = assetService.getAssetById(id);
        return ResponseEntity.ok(ApiResponse.success(asset));
    }
}
