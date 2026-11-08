package com.honeybudget.controller;

import com.honeybudget.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 서버 상태 확인 및 헬스체크 API.
 */
@Tag(name = "Health Check (서버 상태 확인)", description = "서버 상태 확인 및 헬스체크 API")
@RestController
public class HealthCheckController {

    /**
     * 서버 헬스체크 및 기본 정보 조회.
     */
    @Operation(summary = "서버 헬스체크", description = "서버 정상 구동 여부와 현재 서버 시간을 반환합니다.")
    @GetMapping({"/health", "/api/v1/health", "/"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("service", "HoneyBudget Backend");
        data.put("serverTime", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success("서버가 정상적으로 동작 중입니다.", data));
    }
}
