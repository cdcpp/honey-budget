package com.honeybudget.controller;

import com.honeybudget.common.ApiResponse;
import com.honeybudget.common.SwaggerApiResponse;
import com.honeybudget.dto.category.CategoryCreateRequest;
import com.honeybudget.dto.category.CategoryResponse;
import com.honeybudget.dto.category.CategoryUpdateRequest;
import com.honeybudget.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category (중분류 카테고리)", description = "가계부 중분류 카테고리 관리 API")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 중분류 카테고리 등록
     */
    @Operation(summary = "신규 중분류 카테고리 등록", description = "새로운 중분류 카테고리를 특정 대분류 그룹에 소속시켜 등록합니다. 카테고리 코드는 중복될 수 없습니다.")
    @SwaggerApiResponse(responseCode = "201", description = "카테고리 등록 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입력값 검증 실패, 존재하지 않는 대분류 ID, 또는 코드 중복")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryCreateRequest request
    ) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("카테고리가 성공적으로 등록되었습니다.", response));
    }

    /**
     * 중분류 카테고리 수정
     */
    @Operation(summary = "중분류 카테고리 수정", description = "카테고리 ID를 기반으로 카테고리 명칭 및 소속 대분류 그룹을 수정합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "카테고리 수정 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 존재하지 않는 ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @Parameter(description = "수정할 카테고리의 고유 ID", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("카테고리가 성공적으로 수정되었습니다.", response));
    }

    /**
     * 중분류 카테고리 단건 조회
     */
    @Operation(summary = "중분류 카테고리 단건 상세 조회", description = "카테고리 ID를 기반으로 특정 카테고리의 상세 정보를 조회합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "카테고리 조회 성공")
    @SwaggerApiResponse(responseCode = "400", description = "존재하지 않는 카테고리 ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @Parameter(description = "조회할 카테고리의 고유 ID", example = "1")
            @PathVariable("id") Long id
    ) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 중분류 카테고리 목록 조회 (대분류 그룹 ID 필터 옵션)
     */
    @Operation(summary = "중분류 카테고리 목록 조회", description = "등록된 카테고리 목록을 코드순으로 조회합니다. categoryGroupId 파라미터로 특정 대분류 그룹의 카테고리만 필터링할 수 있습니다.")
    @SwaggerApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @Parameter(description = "대분류 그룹 고유 ID (미입력 시 전체 조회)", example = "1")
            @RequestParam(value = "categoryGroupId", required = false)
            Long categoryGroupId
    ) {
        List<CategoryResponse> responses = categoryService.getAllCategories(categoryGroupId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 중분류 카테고리 삭제
     */
    @Operation(summary = "중분류 카테고리 삭제", description = "카테고리 ID를 기반으로 특정 카테고리를 삭제합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "카테고리 삭제 성공")
    @SwaggerApiResponse(responseCode = "400", description = "존재하지 않는 카테고리 ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "삭제할 카테고리의 고유 ID", example = "1")
            @PathVariable("id") Long id
    ) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok("카테고리가 성공적으로 삭제되었습니다."));
    }
}
