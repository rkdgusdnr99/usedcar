package hello.usedcar.web.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TradeApiTest {

    @Autowired
    MockMvc mockMvc;

    String registerCar(String carNumber) throws Exception {
        return mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""" 
                                {"carNumber": "%s", "manufacturer": "기아", "modelName": "EV9", 
                                 "modelYear": 2024, "mileage": 3000, "price": 6500} 
                                """.formatted(carNumber)))
                .andReturn().getResponse().getHeader("Location"); // /api/cars/{id}
    }

    String buyJson(String buyerName) {
        return """ 
                {"buyerName": "%s"} 
                """.formatted(buyerName);
    }

    @Test
    @DisplayName("구매하면 201, 같은 차를 또 사면 409 ALREADY_SOLD")
    void buy() throws Exception {
        String car = registerCar("81나0001");

        mockMvc.perform(post(car + "/trades").contentType(MediaType.APPLICATION_JSON).content(buyJson("홍길동")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.buyerName").value("홍길동"))
                .andExpect(jsonPath("$.price").value(6500));

        mockMvc.perform(get(car))
                .andExpect(jsonPath("$.status").value("SOLD"));

        mockMvc.perform(post(car + "/trades").contentType(MediaType.APPLICATION_JSON).content(buyJson("김철수")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ALREADY_SOLD"));
    }

    @Test
    @DisplayName("체크 예외(본인인증 실패)는 400, 런타임 예외는 500")
    void failures() throws Exception {
        String car = registerCar("81나0002");

        mockMvc.perform(post(car + "/trades").contentType(MediaType.APPLICATION_JSON).content(buyJson("인증실패")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VERIFICATION_FAILED"));

        mockMvc.perform(post(car + "/trades").contentType(MediaType.APPLICATION_JSON).content(buyJson("시스템오류")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL"));

        mockMvc.perform(get(car))
                .andExpect(jsonPath("$.status").value("SALE")); // 둘 다 롤백돼서 여전히 판매중
    }

    @Test
    @DisplayName("없는 차는 404, 구매자 이름이 비면 400")
    void badRequests() throws Exception {
        mockMvc.perform(post("/api/cars/99999/trades").contentType(MediaType.APPLICATION_JSON).content(buyJson("홍길동")))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/cars/1/trades").contentType(MediaType.APPLICATION_JSON).content(buyJson("")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"));
    }
}