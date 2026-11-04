package hello.usedcar.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class CarControllerTest {

    @Autowired
    MockMvc mockMvc;

    // 폼 전송(application/x-www-form-urlencoded)으로 차량을 등록하고 리다이렉트 주소를 돌려준다
    String addCar(String carNumber, String modelName) throws Exception {
        return mockMvc.perform(post("/cars/add")
                        .param("carNumber", carNumber)
                        .param("manufacturer", "기아")
                        .param("modelName", modelName)
                        .param("modelYear", "2022")
                        .param("mileage", "21000")
                        .param("price", "2480"))
                .andReturn().getResponse().getRedirectedUrl();
    }

    @Test
    @DisplayName("등록 폼을 POST하면 상세 화면으로 리다이렉트된다 (PRG)")
    void add() throws Exception {
        mockMvc.perform(post("/cars/add")
                        .param("carNumber", "10가1000")
                        .param("manufacturer", "기아")
                        .param("modelName", "K5")
                        .param("modelYear", "2022")
                        .param("mileage", "21000")
                        .param("price", "2480"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/cars/*?status=true"));
    }

    @Test
    @DisplayName("상세 화면은 타임리프로 렌더링된다")
    void detail() throws Exception {
        String redirect = addCar("20나2000", "쏘렌토"); // /cars/{id}?status=true

        mockMvc.perform(get(redirect))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/detail"))
                .andExpect(content().string(containsString("20나2000")))
                .andExpect(content().string(containsString("저장되었습니다.")))
                .andExpect(content().string(containsString("2,480만원")));
    }

    @Test
    @DisplayName("목록 화면은 검색 조건으로 걸러진다")
    void list() throws Exception {
        addCar("30다3000", "모닝");

        mockMvc.perform(get("/cars").param("modelName", "모닝"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/list"))
                .andExpect(model().attributeExists("cars", "cond", "statuses"))
                .andExpect(content().string(containsString("30다3000")));
    }

    @Test
    @DisplayName("수정 폼을 POST하면 상세 화면으로 리다이렉트되고 값이 바뀐다")
    void edit() throws Exception {
        String detailUrl = addCar("40라4000", "레이").replace("?status=true", "");

        mockMvc.perform(post(detailUrl + "/edit")
                        .param("mileage", "30000")
                        .param("price", "990"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/cars/*"));

        mockMvc.perform(get(detailUrl))
                .andExpect(content().string(containsString("990만원")))
                .andExpect(content().string(containsString("30,000km")));
    }
}
