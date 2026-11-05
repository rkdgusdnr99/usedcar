package hello.usedcar.web.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiExceptionTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("없는 차량은 404와 NOT_FOUND 코드")
    void notFound() throws Exception {
        mockMvc.perform(get("/api/cars/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("차량을 찾을 수 없습니다. id=99999"));

        mockMvc.perform(patch("/api/cars/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""" 
                                {"mileage": 1000, "price": 1000}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("검증 실패는 400과 필드별 오류 목록 (메시지는 messages.properties)")
    void validation() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""" 
                                {"carNumber": "ABC", "manufacturer": "현대", "modelName": "쏘나타", 
                                 "modelYear": 2021, "mileage": 1000, "price": 5} 
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"))
                .andExpect(jsonPath("$.errors[*].field", hasItem("carNumber")))
                .andExpect(jsonPath("$.errors[*].message", hasItem("10 ~ 100000 사이로 입력해주세요.")));
    }

    @Test
    @DisplayName("이미 있는 차량번호로 등록하면 409 Conflict")
    void duplicate() throws Exception {
        String json = """ 
                {"carNumber": "88아8888", "manufacturer": "현대", "modelName": "투싼", 
                 "modelYear": 2022, "mileage": 30000, "price": 2300} 
                """;
        mockMvc.perform(post("/api/cars").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cars").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE"));
    }

    @Test
    @DisplayName("JSON을 객체로 못 바꾸면 400과 BAD_JSON")
    void badJson() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""" 
                                {"price": "이천만원"} 
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_JSON"));
    }

    @Test
    @DisplayName("화면 요청은 JSON이 아니라 404 상태(오류 페이지)로 간다")
    void screenNotFound() throws Exception {
        mockMvc.perform(get("/cars/99999"))
                .andExpect(status().isNotFound());
    }
}
