package hello.usedcar.web.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CarApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    // 차량을 하나 등록하고, 응답의 Location 헤더(/api/cars/{id})를 돌려준다
    String registerCar(String carNumber, String modelName, int price) throws Exception {
        String json = """ 
                {"carNumber": "%s", "manufacturer": "현대", "modelName": "%s", 
                 "modelYear": 2021, "mileage": 42000, "price": %d} 
                """.formatted(carNumber, modelName, price);

        return mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    @DisplayName("POST로 등록하면 201 Created와 Location 헤더가 온다")
    void register() throws Exception {
        String json = """ 
                {"carNumber": "11가1111", "manufacturer": "현대", "modelName": "쏘나타", 
                 "modelYear": 2021, "mileage": 42000, "price": 2150} 
                """;

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/cars/")))
                .andExpect(jsonPath("$.modelName").value("쏘나타"))
                .andExpect(jsonPath("$.status").value("SALE"));
    }

    @Test
    @DisplayName("GET /api/cars/{id}: 있으면 200, 없으면 404")
    void findOne() throws Exception {
        String location = registerCar("22나2222", "아반떼", 1500);

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carNumber").value("22나2222"));

        mockMvc.perform(get("/api/cars/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("쿼리 파라미터는 @ModelAttribute로 검색 조건 객체에 담긴다")
    void search() throws Exception {
        registerCar("33다3333", "그랜저", 3500);
        registerCar("44띱4444", "그랜저", 2800);

        mockMvc.perform(get("/api/cars")
                        .param("modelName", "그랜저")
                        .param("maxPrice", "3000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[*].price", everyItem(lessThanOrEqualTo(3000))));
    }

    @Test
    @DisplayName("PATCH로 가격·주행거리만 수정한다")
    void update() throws Exception {
        String location = registerCar("55마5555", "K5", 1900);

        mockMvc.perform(patch(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""" 
                                {"mileage": 50000, "price": 1800} 
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(1800))
                .andExpect(jsonPath("$.mileage").value(50000))
                .andExpect(jsonPath("$.modelName").value("K5")); // 나머지는 그대로
    }
}
