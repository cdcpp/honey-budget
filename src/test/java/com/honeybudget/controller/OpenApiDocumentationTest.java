package com.honeybudget.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Swagger OpenAPI JSON 명세서 엔드포인트(/v3/api-docs)가 200 OK와 상세 OpenAPI 스펙을 반환한다")
    void testOpenApiJsonDocs() throws Exception {
        mockMvc.perform(get("/v3/api-docs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("HoneyBudget API"))
                .andExpect(jsonPath("$.info.version").value("v1.0.0"))
                .andExpect(jsonPath("$.paths['/api/v1/assets']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/assets'].post.summary").value("신규 자산 등록"))
                .andExpect(jsonPath("$.paths['/api/v1/assets'].get.summary").value("전체 자산 목록 조회"))
                .andExpect(jsonPath("$.paths['/api/v1/assets/{id}'].get.summary").value("자산 단건 상세 조회"))
                .andExpect(jsonPath("$.components.schemas['AssetRequest']").exists())
                .andExpect(jsonPath("$.components.schemas['AssetResponse']").exists());
    }

    @Test
    @DisplayName("Swagger UI 엔드포인트(/swagger-ui.html) 요청 시 실제 UI 페이지로 리다이렉트된다")
    void testSwaggerUiEndpoint() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("/swagger-ui/index.html")));
    }
}
