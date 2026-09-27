package com.honeybudget.controller;

import com.honeybudget.common.ApiResponse;
import com.honeybudget.common.SwaggerApiResponse;
import com.honeybudget.dto.categorygroup.CategoryGroupCreateRequest;
import com.honeybudget.dto.categorygroup.CategoryGroupResponse;
import com.honeybudget.dto.categorygroup.CategoryGroupUpdateRequest;
import com.honeybudget.service.CategoryGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CategoryGroup (대분류 그룹)", description = "카테고리 대분류 그룹 관리 API")
@RestController
@RequestMapping("/api/v1/category-groups")
@RequiredArgsConstructor
public class CategoryGroupController {

    private final CategoryGroupService categoryGroupService;

    /**
     * 대분류 그룹 등록
     */
    @Operation(summary = "신규 대분류 그룹 등록", description = "새로운 대분류 그룹(예: 수입, 지출)을 등록합니다. 대분류 코드는 중복될 수 없습니다.")
    @SwaggerApiResponse(responseCode = "201", description = "대분류 그룹 등록 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 대분류 코드 중복")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryGroupResponse>> createCategoryGroup(
            @Valid @RequestBody CategoryGroupCreateRequest request
    ) {
        CategoryGroupResponse response = categoryGroupService.createCategoryGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("대분류 그룹이 성공적으로 등록되었습니다.", response));
    }

    /**
     * 대분류 그룹 수정
     */
    @Operation(summary = "대분류 그룹 수정", description = "대분류 그룹 ID를 기반으로 대분류 그룹 명칭을 수정합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "대분류 그룹 수정 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 존재하지 않는 ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryGroupResponse>> updateCategoryGroup(
            @Parameter(description = "수정할 대분류 그룹 고유 ID", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryGroupUpdateRequest request
    ) {
        CategoryGroupResponse response = categoryGroupService.updateCategoryGroup(id, request);
        return ResponseEntity.ok(ApiResponse.success("대분류 그룹이 성공적으로 수정되었습니다.", response));
    }

    /**
     * 대분류 그룹 단건 조회
     */
    @Operation(summary = "대분류 그룹 단건 상세 조회", description = "대분류 그룹 ID를 기반으로 특정 그룹의 상세 정보를 조회합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "대분류 그룹 조회 성공")
    @SwaggerApiResponse(responseCode = "400", description = "존재하지 않는 대분류 그룹 ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryGroupResponse>> getCategoryGroupById(
            @Parameter(description = "조회할 대분류 그룹 고유 ID", example = "1")
            @PathVariable("id") Long id
    ) {
        CategoryGroupResponse response = categoryGroupService.getCategoryGroupById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 대분류 그룹 전체 목록 조회
     */
    @Operation(summary = "대분류 그룹 전체 목록 조회", description = "등록된 모든 대분류 그룹 목록을 코드순으로 조회합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "대분류 그룹 목록 조회 성공")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryGroupResponse>>> getAllCategoryGroups() {
        List<CategoryGroupResponse> responses = categoryGroupService.getAllCategoryGroups();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 대분류 그룹 삭제
     */
    @Operation(summary = "대분류 그룹 삭제", description = "대분류 그룹 ID를 기반으로 대분류 그룹을 삭제합니다. 하위 카테고리가 남아있는 경우 삭제할 수 없습니다.")
    @SwaggerApiResponse(responseCode = "200", description = "대분류 그룹 삭제 성공")
    @SwaggerApiResponse(responseCode = "400", description = "존재하지 않는 ID 또는 하위 카테고리 잔존")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategoryGroup(
            @Parameter(description = "삭제할 대분류 그룹 고유 ID", example = "1")
            @PathVariable("id") Long id
    ) {
        categoryGroupService.deleteCategoryGroup(id);
        return ResponseEntity.ok(ApiResponse.ok("대분류 그룹이 성공적으로 삭제되었습니다."));
    }
}
