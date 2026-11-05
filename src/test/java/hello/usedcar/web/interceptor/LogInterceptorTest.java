package hello.usedcar.web.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LogInterceptorTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("화면·API 요청에는 X-Request-Id 헤더가 붙는다")
    void requestIdHeader() throws Exception {
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-Id", matchesPattern("[0-9a-f]{8}")));

        mockMvc.perform(get("/cars"))
                .andExpect(header().exists("X-Request-Id"));
    }

    @Test
    @DisplayName("예외로 404가 나도 인터셉터는 돈다")
    void notFound() throws Exception {
        mockMvc.perform(get("/api/cars/99999"))
                .andExpect(status().isNotFound())
                .andExpect(header().exists("X-Request-Id"));
    }

    @Test
    @DisplayName("제외한 경로(정적 파일)에는 적용되지 않는다")
    void excluded() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("X-Request-Id"));
    }
}