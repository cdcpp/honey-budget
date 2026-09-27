package com.honeybudget.controller;

import com.honeybudget.common.ApiResponse;
import com.honeybudget.common.SwaggerApiResponse;
import com.honeybudget.dto.account.AccountBalanceRequest;
import com.honeybudget.dto.account.AccountCreateRequest;
import com.honeybudget.dto.account.AccountResponse;
import com.honeybudget.dto.account.AccountUpdateRequest;
import com.honeybudget.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Account (계좌)", description = "가계부 계좌 관리 API")
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 계좌 등록 (Create)
     */
    @Operation(summary = "신규 계좌 등록", description = "새로운 계좌를 등록합니다. 동일한 금융기관 내 계좌번호는 중복될 수 없습니다.")
    @SwaggerApiResponse(responseCode = "201", description = "계좌 등록 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입력값 검증 실패, 존재하지 않는 카테고리 ID, 또는 계좌번호 중복")
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody AccountCreateRequest request
    ) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("계좌가 성공적으로 등록되었습니다.", response));
    }

    /**
     * 계좌 정보 수정 (Update)
     */
    @Operation(summary = "계좌 정보 수정", description = "계좌 ID를 기반으로 계좌 명칭, 메모 및 소속 카테고리를 수정합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "계좌 수정 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 존재하지 않는 ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @Parameter(description = "수정할 계좌의 고유 ID", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody AccountUpdateRequest request
    ) {
        AccountResponse response = accountService.updateAccount(id, request);
        return ResponseEntity.ok(ApiResponse.success("계좌 정보가 성공적으로 수정되었습니다.", response));
    }

    /**
     * 계좌 단건 조회 (Read)
     */
    @Operation(summary = "계좌 단건 상세 조회", description = "계좌 ID를 기반으로 특정 계좌의 상세 정보를 조회합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "계좌 조회 성공")
    @SwaggerApiResponse(responseCode = "400", description = "존재하지 않는 계좌 ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(
            @Parameter(description = "조회할 계좌의 고유 ID", example = "1")
            @PathVariable("id") Long id
    ) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 계좌 목록 조회 (Read)
     */
    @Operation(summary = "계좌 목록 조회", description = "등록된 계좌 목록을 최신순으로 조회합니다. useYn(활성 여부), categoryId(카테고리 ID) 파라미터로 필터링할 수 있습니다.")
    @SwaggerApiResponse(responseCode = "200", description = "계좌 목록 조회 성공")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts(
            @Parameter(description = "계좌 활성 여부 (true: 사용중, false: 해지/비활성, 미입력 시 전체)", example = "true")
            @RequestParam(value = "useYn", required = false) Boolean useYn,
            @Parameter(description = "카테고리 고유 ID (미입력 시 전체)", example = "1")
            @RequestParam(value = "categoryId", required = false) Long categoryId
    ) {
        List<AccountResponse> responses = accountService.getAllAccounts(useYn, categoryId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 계좌 비활성화 / 해지 처리 (Soft Delete)
     */
    @Operation(summary = "계좌 비활성화/해지 (삭제)", description = "계좌 ID를 기반으로 해당 계좌를 비활성화(Soft Delete, useYn = false) 처리합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "계좌 비활성화 성공")
    @SwaggerApiResponse(responseCode = "400", description = "존재하지 않는 계좌 ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @Parameter(description = "삭제(비활성화)할 계좌의 고유 ID", example = "1")
            @PathVariable("id") Long id
    ) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok(ApiResponse.ok("계좌가 성공적으로 비활성화(해지)되었습니다."));
    }

    /**
     * 계좌 잔액 입금
     */
    @Operation(summary = "계좌 잔액 입금", description = "지정한 계좌에 금액을 입금하여 잔액을 증가시킵니다.")
    @SwaggerApiResponse(responseCode = "200", description = "입금 처리 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입금 금액 오류 또는 존재하지 않는 계좌 ID")
    @PostMapping("/{id}/deposit")
    public ResponseEntity<ApiResponse<AccountResponse>> deposit(
            @Parameter(description = "입금할 계좌의 고유 ID", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody AccountBalanceRequest request
    ) {
        AccountResponse response = accountService.deposit(id, request.getAmount());
        return ResponseEntity.ok(ApiResponse.success("입금이 성공적으로 완료되었습니다.", response));
    }

    /**
     * 계좌 잔액 출금
     */
    @Operation(summary = "계좌 잔액 출금", description = "지정한 계좌에서 금액을 출금하여 잔액을 차감합니다. 잔액이 부족하면 실패합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "출금 처리 성공")
    @SwaggerApiResponse(responseCode = "400", description = "출금 금액 오류, 잔액 부족 또는 존재하지 않는 계좌 ID")
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<ApiResponse<AccountResponse>> withdraw(
            @Parameter(description = "출금할 계좌의 고유 ID", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody AccountBalanceRequest request
    ) {
        AccountResponse response = accountService.withdraw(id, request.getAmount());
        return ResponseEntity.ok(ApiResponse.success("출금이 성공적으로 완료되었습니다.", response));
    }
}
