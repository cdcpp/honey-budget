package com.honeybudget.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("헬스체크 엔드포인트(/health) 호출 시 성공 응답을 반환한다")
    void healthCheckSuccess() throws Exception {
        mockMvc.perform(get("/health").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("UP")))
                .andExpect(jsonPath("$.data.service", is("HoneyBudget Backend")));
    }

    @Test
    @DisplayName("루트 경로(/) 호출 시에도 헬스체크 정상 응답을 반환한다")
    void rootEndpointSuccess() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("UP")));
    }
}
