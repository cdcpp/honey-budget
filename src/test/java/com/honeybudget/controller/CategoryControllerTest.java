package com.honeybudget.controller;

import com.honeybudget.domain.Category;
import com.honeybudget.domain.CategoryGroup;
import com.honeybudget.repository.CategoryGroupRepository;
import com.honeybudget.repository.CategoryRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryGroupRepository categoryGroupRepository;

    private CategoryGroup sampleGroup;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAllInBatch();
        categoryGroupRepository.deleteAllInBatch();

        sampleGroup = categoryGroupRepository.save(
                CategoryGroup.builder().groupCode("200").groupName("지출").build()
        );
    }

    @Test
    @DisplayName("중분류 카테고리 등록 성공: POST /api/v1/categories")
    void testCreateCategorySuccess() throws Exception {
        String requestJson = String.format("""
                {
                    "categoryGroupId": %d,
                    "categoryCode": "201",
                    "categoryName": "생활비 통장"
                }
                """, sampleGroup.getId());

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.categoryCode", is("201")))
                .andExpect(jsonPath("$.data.categoryName", is("생활비 통장")))
                .andExpect(jsonPath("$.data.categoryGroupId", is(sampleGroup.getId().intValue())))
                .andExpect(jsonPath("$.data.categoryGroupCode", is("200")))
                .andExpect(jsonPath("$.data.categoryGroupName", is("지출")))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    @DisplayName("중분류 카테고리 등록 실패: 중복된 코드 시 400 Bad Request 반환")
    void testCreateDuplicateCodeThrowsBadRequest() throws Exception {
        String requestJson = String.format("""
                {
                    "categoryGroupId": %d,
                    "categoryCode": "201",
                    "categoryName": "생활비 통장"
                }
                """, sampleGroup.getId());

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message").value("이미 존재하는 중분류 카테고리 코드입니다: 201"));
    }

    @Test
    @DisplayName("중분류 카테고리 수정 성공: PUT /api/v1/categories/{id}")
    void testUpdateCategorySuccess() throws Exception {
        Category category = categoryRepository.save(
                Category.builder().group(sampleGroup).categoryCode("201").categoryName("생활비").build()
        );

        String updateJson = """
                {
                    "categoryName": "생활비 (수정)"
                }
                """;

        mockMvc.perform(put("/api/v1/categories/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.categoryName", is("생활비 (수정)")));
    }

    @Test
    @DisplayName("중분류 카테고리 단건 조회: GET /api/v1/categories/{id}")
    void testGetCategoryByIdSuccess() throws Exception {
        Category category = categoryRepository.save(
                Category.builder().group(sampleGroup).categoryCode("201").categoryName("생활비").build()
        );

        mockMvc.perform(get("/api/v1/categories/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.categoryCode", is("201")));
    }

    @Test
    @DisplayName("중분류 카테고리 목록 조회 및 대분류 그룹 필터링: GET /api/v1/categories")
    void testGetAllCategoriesAndFilter() throws Exception {
        CategoryGroup incomeGroup = categoryGroupRepository.save(
                CategoryGroup.builder().groupCode("100").groupName("수입").build()
        );

        categoryRepository.save(Category.builder().group(incomeGroup).categoryCode("101").categoryName("급여").build());
        categoryRepository.save(Category.builder().group(sampleGroup).categoryCode("201").categoryName("생활비").build());

        // 전체 조회 -> 2건
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        // 특정 그룹 필터 조회 -> 1건
        mockMvc.perform(get("/api/v1/categories")
                        .param("categoryGroupId", sampleGroup.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].categoryCode", is("201")));
    }

    @Test
    @DisplayName("중분류 카테고리 삭제 성공: DELETE /api/v1/categories/{id}")
    void testDeleteCategorySuccess() throws Exception {
        Category category = categoryRepository.save(
                Category.builder().group(sampleGroup).categoryCode("201").categoryName("생활비").build()
        );

        mockMvc.perform(delete("/api/v1/categories/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message").value("카테고리가 성공적으로 삭제되었습니다."));

        mockMvc.perform(get("/api/v1/categories/" + category.getId()))
                .andExpect(status().isBadRequest());
    }
}
