package com.honeybudget.controller;

import com.honeybudget.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssetRepository assetRepository;

    @BeforeEach
    void setUp() {
        assetRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("자산 생성 API 성공: POST /api/v1/assets")
    void testCreateAsset() throws Exception {
        String requestJson = """
                {
                    "name": "생활비통장",
                    "role": "EXPENSE"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("생활비통장")))
                .andExpect(jsonPath("$.data.role", is("EXPENSE")))
                .andExpect(jsonPath("$.data.roleDescription", is("지출용")))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    @DisplayName("자산 생성 실패: 중복된 이름")
    void testCreateAssetDuplicateName() throws Exception {
        String requestJson = """
                {
                    "name": "생활비통장",
                    "role": "EXPENSE"
                }
                """;

        // 1회차 성공
        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        // 2회차 중복 시도 -> 400 Bad Request
        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message").value("이미 존재하는 자산 이름입니다: 생활비통장"));
    }

    @Test
    @DisplayName("자산 생성 실패: 이름 공백 유효성 검증")
    void testCreateAssetBlankName() throws Exception {
        String requestJson = """
                {
                    "name": "   ",
                    "role": "EXPENSE"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("전체 자산 목록 조회: GET /api/v1/assets")
    void testGetAllAssets() throws Exception {
        // 2개 생성
        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        { "name": "생활비통장", "role": "EXPENSE" }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        { "name": "비상금통장", "role": "RESERVE" }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));
    }
}
