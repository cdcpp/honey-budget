package com.honeybudget.controller;

import com.honeybudget.common.ApiResponse;
import com.honeybudget.common.SwaggerApiResponse;
import com.honeybudget.dto.accountyearmonth.AccountYearMonthCreateRequest;
import com.honeybudget.dto.accountyearmonth.AccountYearMonthResponse;
import com.honeybudget.dto.accountyearmonth.AccountYearMonthUpdateRequest;
import com.honeybudget.dto.accountyearmonth.AnnualStatementResponse;
import com.honeybudget.service.AccountYearMonthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 계좌 연간 월별 금액 및 명세표 관리 API.
 */
@Tag(name = "AccountYearMonth (계좌 월별 금액)", description = "연간 수입/저축/지출 명세표의 계좌별 월별 금액 관리 API")
@RestController
@RequestMapping("/api/v1/account-year-months")
@RequiredArgsConstructor
public class AccountYearMonthController {

    private final AccountYearMonthService accountYearMonthService;

    /** 계좌의 연간 1~12월 금액 행을 등록한다. (계좌+연도당 1건) */
    @Operation(summary = "계좌 연간 월별 금액 등록", description = "계좌+연도 단위로 1~12월 금액 행을 등록합니다.")
    @SwaggerApiResponse(responseCode = "201", description = "등록 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입력값 검증 실패, 존재하지 않는 계좌 또는 중복 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<AccountYearMonthResponse>> create(
            @Valid @RequestBody AccountYearMonthCreateRequest request
    ) {
        AccountYearMonthResponse response = accountYearMonthService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("월별 금액이 성공적으로 등록되었습니다.", response));
    }

    /** 지정한 월(1~12월)의 금액을 수정한다. */
    @Operation(summary = "월별 금액 수정", description = "요청에 포함된 월(1~12)의 금액만 수정합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "수정 성공")
    @SwaggerApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 존재하지 않는 ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountYearMonthResponse>> update(
            @Parameter(description = "수정할 행의 고유 ID", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody AccountYearMonthUpdateRequest request
    ) {
        AccountYearMonthResponse response = accountYearMonthService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("월별 금액이 성공적으로 수정되었습니다.", response));
    }

    /** 월별 금액 행 단건을 상세 조회한다. */
    @Operation(summary = "월별 금액 단건 조회")
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @SwaggerApiResponse(responseCode = "400", description = "존재하지 않는 ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountYearMonthResponse>> getById(
            @Parameter(description = "조회할 행의 고유 ID", example = "1")
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(accountYearMonthService.getById(id)));
    }

    /** 연도별 계좌의 1~12월 금액 목록을 조회한다. 데이터가 없는 계좌는 0원으로 채운다. */
    @Operation(summary = "연도별 계좌 월별 금액 목록 조회",
            description = "특정 연도의 모든 활성 계좌의 1~12월 금액 목록을 조회합니다. 데이터가 없는 계좌는 0원으로 자동 준비됩니다.")
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/year/{year}")
    public ResponseEntity<ApiResponse<List<AccountYearMonthResponse>>> getByYear(
            @Parameter(description = "조회할 연도", example = "2026")
            @PathVariable("year") Integer year
    ) {
        return ResponseEntity.ok(ApiResponse.success(accountYearMonthService.getByYear(year)));
    }

    /** 특정 연도의 종합 명세표(계좌별 내역, 대분류 소계, 월별 순수익)를 조회한다. */
    @Operation(summary = "연간 수입/저축/지출 명세표 종합 조회",
            description = "특정 연도의 계좌별 1~12월 내역과 대분류 그룹별(수입, 저축, 지출) 월별 소계 및 연간 총 순수익을 종합 조회합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/year/{year}/statement")
    public ResponseEntity<ApiResponse<AnnualStatementResponse>> getAnnualStatement(
            @Parameter(description = "조회할 연도", example = "2026")
            @PathVariable("year") Integer year
    ) {
        return ResponseEntity.ok(ApiResponse.success(accountYearMonthService.getAnnualStatement(year)));
    }

    /** 연도 또는 계좌 ID 조건으로 월별 금액 목록을 조회한다. */
    @Operation(summary = "월별 금액 목록 조회 (쿼리 파라미터 필터)",
            description = "year, accountId로 필터링합니다. 둘 다 없으면 올해 전체 계좌를 조회합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "목록 조회 성공")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountYearMonthResponse>>> getAll(
            @Parameter(description = "연도 (미입력 시 올해, accountId 지정 시 전체 연도)", example = "2026")
            @RequestParam(value = "year", required = false) Integer year,
            @Parameter(description = "계좌 ID (미입력 시 전체)", example = "1")
            @RequestParam(value = "accountId", required = false) Long accountId
    ) {
        return ResponseEntity.ok(ApiResponse.success(accountYearMonthService.getAll(year, accountId)));
    }
}
