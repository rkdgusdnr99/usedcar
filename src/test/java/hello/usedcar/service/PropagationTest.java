package hello.usedcar.service;

import hello.usedcar.domain.Car;
import hello.usedcar.dto.CarSaveRequest;
import hello.usedcar.repository.mybatis.TradeLogMapper;
import hello.usedcar.repository.mybatis.TradeMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PropagationTest {

    @Autowired TradeService tradeService;
    @Autowired CarService carService;
    @Autowired TradeMapper tradeMapper;
    @Autowired TradeLogMapper tradeLogMapper;
    @Autowired OuterService outerService;

    Car newCar(String carNumber) {
        return carService.register(new CarSaveRequest(carNumber, "기아", "니로", 2022, 25000, 2300));
    }

    @Test
    @DisplayName("구매 성공: 거래 1건, 시도 기록 1건")
    void success() throws Exception {
        Car car = newCar("82다0001");

        tradeService.buy(car.getId(), "홍길동");

        assertThat(tradeMapper.countByCarId(car.getId())).isEqualTo(1);
        assertThat(tradeLogMapper.countByCarId(car.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("REQUIRES_NEW: 구매가 롤백돼도 시도 기록은 남는다")
    void logSurvivesRollback() {
        Car car = newCar("82다0002");

        assertThatThrownBy(() -> tradeService.buy(car.getId(), "시스템오류"))
                .isInstanceOf(IllegalStateException.class);

        assertThat(tradeMapper.countByCarId(car.getId())).isZero();       // 구매는 롤백
        assertThat(tradeLogMapper.countByCarId(car.getId())).isEqualTo(1); // 시도 기록은 커밋돼 있음
    }

    @Test
    @DisplayName("REQUIRED 함정: 안쪽에서 난 예외를 바깥에서 잡아도 커밋 시점에 UnexpectedRollbackException")
    void requiredInnerFailure() {
        assertThatThrownBy(() -> outerService.callInnerAndCatch())
                .isInstanceOf(UnexpectedRollbackException.class);
    }

    // ---- REQUIRED 함정 실험용 빈 ----

    @TestConfiguration
    static class Config {
        @Bean
        InnerService innerService() {
            return new InnerService();
        }

        @Bean
        OuterService outerService(InnerService innerService) {
            return new OuterService(innerService);
        }
    }

    static class InnerService {
        @Transactional // 기본 = REQUIRED: 바깥 트랜잭션에 참여한다
        public void fail() {
            throw new IllegalStateException("안쪽 실패");
        }
    }

    @RequiredArgsConstructor
    static class OuterService {
        private final InnerService innerService;

        @Transactional
        public void callInnerAndCatch() {
            try {
                innerService.fail();   // 안쪽에서 예외 → 트랜잭션 전체에 "롤백만 가능" 표시가 붙는다
            } catch (IllegalStateException e) {
                // 잡아서 정상 흐름으로 돌렸지만...
            }
        } // 여기서 커밋을 시도 → 롤백 표시 때문에 커밋 불가 → UnexpectedRollbackException
    }
}