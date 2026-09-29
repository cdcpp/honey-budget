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
class CategoryGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryGroupRepository categoryGroupRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAllInBatch();
        categoryGroupRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("대분류 그룹 등록 성공: POST /api/v1/category-groups")
    void testCreateCategoryGroupSuccess() throws Exception {
        String requestJson = """
                {
                    "categoryGroupCode": "100",
                    "categoryGroupName": "수입"
                }
                """;

        mockMvc.perform(post("/api/v1/category-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.categoryGroupCode", is("100")))
                .andExpect(jsonPath("$.data.categoryGroupName", is("수입")))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    @DisplayName("대분류 그룹 등록 실패: 중복된 코드 등록 시 400 Bad Request 반환")
    void testCreateDuplicateCodeThrowsBadRequest() throws Exception {
        String requestJson = """
                {
                    "categoryGroupCode": "100",
                    "categoryGroupName": "수입"
                }
                """;

        mockMvc.perform(post("/api/v1/category-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/category-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message").value("이미 존재하는 대분류 그룹 코드입니다: 100"));
    }

    @Test
    @DisplayName("대분류 그룹 수정 성공: PUT /api/v1/category-groups/{id}")
    void testUpdateCategoryGroupSuccess() throws Exception {
        CategoryGroup group = categoryGroupRepository.save(
                CategoryGroup.builder().groupCode("100").groupName("수입").build()
        );

        String updateJson = """
                {
                    "categoryGroupName": "수입 (급여/상여)"
                }
                """;

        mockMvc.perform(put("/api/v1/category-groups/" + group.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.categoryGroupName", is("수입 (급여/상여)")));
    }

    @Test
    @DisplayName("대분류 그룹 단건 조회: GET /api/v1/category-groups/{id}")
    void testGetCategoryGroupById() throws Exception {
        CategoryGroup group = categoryGroupRepository.save(
                CategoryGroup.builder().groupCode("100").groupName("수입").build()
        );

        mockMvc.perform(get("/api/v1/category-groups/" + group.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.categoryGroupCode", is("100")));
    }

    @Test
    @DisplayName("대분류 그룹 전체 목록 조회: GET /api/v1/category-groups")
    void testGetAllCategoryGroups() throws Exception {
        categoryGroupRepository.save(CategoryGroup.builder().groupCode("100").groupName("수입").build());
        categoryGroupRepository.save(CategoryGroup.builder().groupCode("200").groupName("지출").build());

        mockMvc.perform(get("/api/v1/category-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    @DisplayName("대분류 그룹 다음 추천 코드 조회 (데이터 없음): GET /api/v1/category-groups/next-code -> 100")
    void testGetNextCategoryGroupCodeEmptyReturns100() throws Exception {
        mockMvc.perform(get("/api/v1/category-groups/next-code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.nextCode", is("100")));
    }

    @Test
    @DisplayName("대분류 그룹 다음 추천 코드 조회 (기존 데이터 존재): GET /api/v1/category-groups/next-code -> 300")
    void testGetNextCategoryGroupCodeWithExistingReturns300() throws Exception {
        categoryGroupRepository.save(CategoryGroup.builder().groupCode("100").groupName("수입").build());
        categoryGroupRepository.save(CategoryGroup.builder().groupCode("200").groupName("지출").build());

        mockMvc.perform(get("/api/v1/category-groups/next-code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.nextCode", is("300")));
    }

    @Test
    @DisplayName("대분류 그룹 삭제 성공: DELETE /api/v1/category-groups/{id}")
    void testDeleteCategoryGroupSuccess() throws Exception {
        CategoryGroup group = categoryGroupRepository.save(
                CategoryGroup.builder().groupCode("100").groupName("수입").build()
        );

        mockMvc.perform(delete("/api/v1/category-groups/" + group.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message").value("대분류 그룹이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("대분류 그룹 삭제 실패: 하위 카테고리가 남아있는 경우 400 Bad Request 반환")
    void testDeleteCategoryGroupWithChildrenThrowsBadRequest() throws Exception {
        CategoryGroup group = categoryGroupRepository.save(
                CategoryGroup.builder().groupCode("100").groupName("수입").build()
        );
        categoryRepository.save(
                Category.builder().group(group).categoryCode("101").categoryName("급여").build()
        );

        mockMvc.perform(delete("/api/v1/category-groups/" + group.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message").value("해당 대분류 그룹에 속한 하위 카테고리가 존재하여 삭제할 수 없습니다. ID: " + group.getId()));
    }
}
