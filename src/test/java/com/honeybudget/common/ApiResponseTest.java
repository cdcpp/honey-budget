package com.honeybudget.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    @DisplayName("성공 응답 생성 및 기본 메시지 확인")
    void successWithData() {
        ApiResponse<String> response = ApiResponse.success("테스트 데이터");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("요청이 성공적으로 처리되었습니다.");
        assertThat(response.getData()).isEqualTo("테스트 데이터");
    }

    @Test
    @DisplayName("커스텀 메시지를 포함한 성공 응답 생성")
    void successWithCustomMessageAndData() {
        ApiResponse<Integer> response = ApiResponse.success("조회 완료", 100);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("조회 완료");
        assertThat(response.getData()).isEqualTo(100);
    }

    @Test
    @DisplayName("데이터 없는 성공 응답(ok) 확인")
    void okResponse() {
        ApiResponse<Void> response = ApiResponse.ok();

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("요청이 성공적으로 처리되었습니다.");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("커스텀 메시지를 포함한 데이터 없는 성공 응답(ok) 확인")
    void okResponseWithCustomMessage() {
        ApiResponse<Void> response = ApiResponse.ok("삭제 완료");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("삭제 완료");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("실패 응답 생성 확인")
    void errorResponse() {
        ApiResponse<Void> response = ApiResponse.error("잘못된 요청입니다.");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("잘못된 요청입니다.");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("에러 데이터가 포함된 실패 응답 생성 확인")
    void errorResponseWithData() {
        Map<String, String> fieldErrors = Map.of("email", "이메일 형식이 올바르지 않습니다.");
        ApiResponse<Map<String, String>> response = ApiResponse.error("유효성 검증 실패", fieldErrors);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("유효성 검증 실패");
        assertThat(response.getData()).isEqualTo(fieldErrors);
    }
}
