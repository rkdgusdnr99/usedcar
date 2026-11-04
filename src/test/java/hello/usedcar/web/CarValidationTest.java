package hello.usedcar.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class CarValidationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("빈 값을 보내면 등록 폼으로 돌아가고 필드마다 오류가 담긴다")
    void emptyForm() throws Exception {
        mockMvc.perform(post("/cars/add"))
                .andExpect(status().isOk())                 // 리다이렉트가 아니라 폼을 다시 보여줌
                .andExpect(view().name("cars/addForm"))
                .andExpect(model().attributeHasFieldErrorCode("car", "carNumber", "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode("car", "price", "NotNull"))
                .andExpect(content().string(containsString("필수 입력 항목입니다.")));
    }

    @Test
    @DisplayName("형식·범위가 틀리면 해당 오류 코드와 메시지가 나온다")
    void wrongValues() throws Exception {
        mockMvc.perform(post("/cars/add")
                        .param("carNumber", "ABC-123")   // 형식 오류
                        .param("manufacturer", "현대")
                        .param("modelName", "쏘나타")
                        .param("modelYear", "1980")      // 범위 오류
                        .param("mileage", "-5")          // 최솟값 오류
                        .param("price", "2150"))
                .andExpect(view().name("cars/addForm"))
                .andExpect(model().attributeHasFieldErrorCode("car", "carNumber", "Pattern"))
                .andExpect(model().attributeHasFieldErrorCode("car", "modelYear", "Range"))
                .andExpect(model().attributeHasFieldErrorCode("car", "mileage", "Min"))
                .andExpect(content().string(containsString("차량번호 형식이 올바르지 않습니다.")))
                .andExpect(content().string(containsString("1990 ~ 2026 사이로 입력해주세요.")))
                .andExpect(content().string(containsString("value=\"ABC-123\""))); // 입력값 유지
    }

    @Test
    @DisplayName("숫자 칸에 글자를 넣으면 typeMismatch 오류가 된다 (400이 아니다)")
    void typeMismatch() throws Exception {
        mockMvc.perform(post("/cars/add")
                        .param("carNumber", "12가3456")
                        .param("manufacturer", "현대")
                        .param("modelName", "쏘나타")
                        .param("modelYear", "2021")
                        .param("mileage", "42000")
                        .param("price", "이천만원"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("car", "price", "typeMismatch"))
                .andExpect(content().string(containsString("숫자를 입력해주세요.")))
                .andExpect(content().string(containsString("value=\"이천만원\"")));
    }

    @Test
    @DisplayName("이미 있는 차량번호는 직접 만든 오류 코드(duplicate)로 막는다")
    void duplicateCarNumber() throws Exception {
        String[] form = {"77사7777", "기아", "K8", "2023", "15000", "3600"};
        postForm(form); // 첫 등록은 성공

        mockMvc.perform(post("/cars/add")
                        .param("carNumber", form[0])
                        .param("manufacturer", form[1])
                        .param("modelName", form[2])
                        .param("modelYear", form[3])
                        .param("mileage", form[4])
                        .param("price", form[5]))
                .andExpect(view().name("cars/addForm"))
                .andExpect(model().attributeHasFieldErrorCode("car", "carNumber", "duplicate"))
                .andExpect(content().string(containsString("이미 등록된 차량번호입니다.")));
    }

    @Test
    @DisplayName("API는 @Valid 실패 시 400 Bad Request")
    void apiValidation() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""" 
                                {"carNumber": "", "price": -1} 
                                """))
                .andExpect(status().isBadRequest());
    }

    void postForm(String[] f) throws Exception {
        mockMvc.perform(post("/cars/add")
                .param("carNumber", f[0]).param("manufacturer", f[1]).param("modelName", f[2])
                .param("modelYear", f[3]).param("mileage", f[4]).param("price", f[5]));
    }
}
