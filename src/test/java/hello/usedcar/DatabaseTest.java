package hello.usedcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseTest {

    @Autowired DataSource dataSource;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("테스트는 H2가 아니라 PostgreSQL의 테스트 전용 DB로 돈다")
    void realDatabase() throws Exception {
        try (Connection con = dataSource.getConnection()) {
            assertThat(con.getMetaData().getDatabaseProductName()).isEqualTo("PostgreSQL");
            assertThat(con.getCatalog()).isEqualTo("usedcar_test");
        }
    }

    @Test
    @DisplayName("Flyway가 V1(스키마)과 V1.1(샘플 데이터)을 순서대로 적용했다")
    void flywayHistory() {
        List<String> versions = jdbcTemplate.queryForList(
                "select version from flyway_schema_history where success order by installed_rank", String.class);

        assertThat(versions).containsExactly("1", "1.1");
    }
}