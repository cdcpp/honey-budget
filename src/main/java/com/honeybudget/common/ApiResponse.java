package com.honeybudget.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * REST API 공통 응답 규격.
 *
 * @param <T> 응답 본문 데이터 타입
 */
@Schema(description = "공통 API 응답 규격")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    @Schema(description = "요청 처리 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 안내 또는 결과 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "응답 본문 데이터")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /** 성공 응답을 생성한다. */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
    }

    /** 메시지와 데이터를 포함한 성공 응답을 생성한다. */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /** 기본 메시지를 가진 빈 성공 응답을 생성한다. */
    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", null);
    }

    /** 메시지만 포함한 빈 성공 응답을 생성한다. */
    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(true, message, null);
    }

    /** 에러 메시지를 포함한 실패 응답을 생성한다. */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /** 에러 메시지와 상세 데이터를 포함한 실패 응답을 생성한다. */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
