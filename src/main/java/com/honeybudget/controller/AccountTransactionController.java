package com.honeybudget.controller;

import com.honeybudget.common.ApiResponse;
import com.honeybudget.common.SwaggerApiResponse;
import com.honeybudget.dto.accounttransaction.AccountTransactionCreateRequest;
import com.honeybudget.dto.accounttransaction.AccountTransactionResponse;
import com.honeybudget.dto.accounttransaction.AccountTransactionUpdateRequest;
import com.honeybudget.service.AccountTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AccountTransaction (계좌 거래 내역)", description = "계좌별 일자 거래(지출) 내역 관리 API")
@RestController
@RequestMapping("/api/v1/account-transactions")
@RequiredArgsConstructor
public class AccountTransactionController {

    private final AccountTransactionService accountTransactionService;

    /**
     * 거래 내역 등록 (Create)
     */
    @Operation(summary = "거래 내역 등록", description = "계좌의 일자별 거래(지출) 내역을 등록합니다. 월별 시트(yearMonth)는 거래 일자로부터 자동 계산됩니다.")
    @SwaggerApiResponse(responseCode = "201", description = "거래 내역 등록 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 존재하지 않는 계좌 ID")
    @PostMapping
    public ResponseEntity<ApiResponse<AccountTransactionResponse>> create(
            @Valid @RequestBody AccountTransactionCreateRequest request
    ) {
        AccountTransactionResponse response = accountTransactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("거래 내역이 성공적으로 등록되었습니다.", response));
    }

    /**
     * 거래 내역 수정 (Update)
     */
    @Operation(summary = "거래 내역 수정", description = "거래 내역 ID를 기반으로 거래 일자, 금액, 사용처/내용, 메모를 수정합니다. 거래 일자 변경 시 yearMonth도 함께 변경됩니다.")
    @SwaggerApiResponse(responseCode = "200", description = "거래 내역 수정 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 존재하지 않는 ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountTransactionResponse>> update(
            @Parameter(description = "수정할 거래 내역의 고유 ID", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody AccountTransactionUpdateRequest request
    ) {
        AccountTransactionResponse response = accountTransactionService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("거래 내역이 성공적으로 수정되었습니다.", response));
    }

    /**
     * 거래 내역 단건 조회 (Read)
     */
    @Operation(summary = "거래 내역 단건 상세 조회", description = "거래 내역 ID를 기반으로 상세 정보를 조회합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "거래 내역 조회 성공")
    @SwaggerApiResponse(responseCode = "400", description = "존재하지 않는 거래 내역 ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountTransactionResponse>> getById(
            @Parameter(description = "조회할 거래 내역의 고유 ID", example = "1")
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(accountTransactionService.getById(id)));
    }

    /**
     * 거래 내역 목록 조회 (Read)
     */
    @Operation(summary = "거래 내역 목록 조회", description = "거래 내역 목록을 거래 일자 최신순으로 조회합니다. accountId(계좌 ID), yearMonth(연월 YYYY-MM) 파라미터로 필터링할 수 있습니다.")
    @SwaggerApiResponse(responseCode = "200", description = "거래 내역 목록 조회 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 연월 형식")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountTransactionResponse>>> getAll(
            @Parameter(description = "계좌 고유 ID (미입력 시 전체)", example = "1")
            @RequestParam(value = "accountId", required = false) Long accountId,
            @Parameter(description = "월별 시트 연월 (YYYY-MM, 미입력 시 전체)", example = "2025-01")
            @RequestParam(value = "yearMonth", required = false) String yearMonth
    ) {
        return ResponseEntity.ok(ApiResponse.success(accountTransactionService.getAll(accountId, yearMonth)));
    }

    /**
     * 거래 내역 삭제 (Delete)
     */
    @Operation(summary = "거래 내역 삭제", description = "거래 내역 ID를 기반으로 해당 거래 내역을 삭제합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "거래 내역 삭제 성공")
    @SwaggerApiResponse(responseCode = "400", description = "존재하지 않는 거래 내역 ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "삭제할 거래 내역의 고유 ID", example = "1")
            @PathVariable("id") Long id
    ) {
        accountTransactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("거래 내역이 성공적으로 삭제되었습니다."));
    }
}
