package hello.usedcar.web.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * data.sql로 들어간 샘플 데이터를 사용한다. (1번 차량: 12가3456 쏘나타, 정비 이력 2건)
 */
@SpringBootTest
@AutoConfigureMockMvc
class MaintenanceAndPriceApiTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("정비 이력 API: 최근 정비가 먼저")
    void maintenances() throws Exception {
        mockMvc.perform(get("/api/cars/1/maintenances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].description").value("브레이크 패드 교체"))
                .andExpect(jsonPath("$[0].maintainedAt").value("2024-05-21"));
    }

    @Test
    @DisplayName("없는 차량의 정비 이력은 404")
    void maintenancesNotFound() throws Exception {
        mockMvc.perform(get("/api/cars/99999/maintenances"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("시세 API: 쿼리 파라미터로 모델명을 받는다")
    void marketPrice() throws Exception {
        mockMvc.perform(get("/api/market-prices").param("modelName", "G80"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName").value("G80"))
                .andExpect(jsonPath("$.carCount").value(1))
                .andExpect(jsonPath("$.avgPrice").value(3900));
    }

    @Test
    @DisplayName("상세 화면에 정비 이력과 시세가 나온다")
    void detailScreen() throws Exception {
        mockMvc.perform(get("/cars/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("엔진오일 교환")))
                .andExpect(content().string(containsString("판매중인 쏘나타")));
    }
}