package hello.usedcar;

import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 테스트 전용 설정 (src/test 에 있으니 운영 코드에는 포함되지 않는다).
 *
 * H2 메모리 DB는 테스트를 돌릴 때마다 새로 만들어졌지만, PostgreSQL은 데이터가 계속 남는다.
 * 그래서 스프링 컨테이너가 뜰 때마다 Flyway로 테이블을 전부 지우고(clean) 처음부터 다시 만든다(migrate).
 * → 테스트는 항상 "V1 스키마 + 샘플 데이터"만 있는 같은 상태에서 시작한다.
 */
@Configuration
public class TestDatabaseConfig {

    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}
