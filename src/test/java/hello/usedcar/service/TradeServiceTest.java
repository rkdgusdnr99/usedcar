package hello.usedcar.service;

import hello.usedcar.domain.Car;
import hello.usedcar.domain.CarStatus;
import hello.usedcar.dto.CarSaveRequest;
import hello.usedcar.exception.AlreadySoldException;
import hello.usedcar.exception.BuyerVerificationException;
import hello.usedcar.repository.mybatis.TradeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 주의: 이 테스트 클래스에는 @Transactional을 붙이지 않는다.
 * 붙이면 테스트 전체가 하나의 트랜잭션이 되어, 서비스의 커밋·롤백을 제대로 관찰할 수 없다.
 * 그래서 테스트마다 서로 다른 차량번호로 새 차를 만들어 쓴다.
 */
@SpringBootTest
class TradeServiceTest {

    @Autowired TradeService tradeService;
    @Autowired CarService carService;
    @Autowired TradeMapper tradeMapper;

    Car newCar(String carNumber) {
        return carService.register(new CarSaveRequest(carNumber, "현대", "아이오닉5", 2023, 9000, 4100));
    }

    @Test
    @DisplayName("TradeService는 스프링이 만든 프록시다 (@Transactional이 동작하는 이유)")
    void proxy() {
        System.out.println("tradeService class = " + tradeService.getClass());
        assertThat(AopUtils.isAopProxy(tradeService)).isTrue();
    }

    @Test
    @DisplayName("정상 구매: 거래가 저장되고 차량은 판매완료")
    void buy() throws Exception {
        Car car = newCar("80가0001");

        tradeService.buy(car.getId(), "홍길동");

        assertThat(tradeMapper.countByCarId(car.getId())).isEqualTo(1);
        assertThat(carService.findCar(car.getId()).getStatus()).isEqualTo(CarStatus.SOLD);
    }

    @Test
    @DisplayName("이미 팔린 차는 AlreadySoldException")
    void alreadySold() throws Exception {
        Car car = newCar("80가0002");
        tradeService.buy(car.getId(), "홍길동");

        assertThatThrownBy(() -> tradeService.buy(car.getId(), "김철수"))
                .isInstanceOf(AlreadySoldException.class);
        assertThat(tradeMapper.countByCarId(car.getId())).isEqualTo(1); // 두 번째 거래는 없음
    }

    @Test
    @DisplayName("런타임 예외 → 롤백: 이미 저장한 거래도 사라진다")
    void runtimeExceptionRollback() {
        Car car = newCar("80가0003");

        assertThatThrownBy(() -> tradeService.buy(car.getId(), "시스템오류"))
                .isInstanceOf(IllegalStateException.class);

        assertThat(tradeMapper.countByCarId(car.getId())).isZero();                       // ① 취소됨
        assertThat(carService.findCar(car.getId()).getStatus()).isEqualTo(CarStatus.SALE); // 여전히 판매중
    }

    @Test
    @DisplayName("체크 예외 → rollbackFor를 지정했으니 롤백")
    void checkedExceptionRollback() {
        Car car = newCar("80가0004");

        assertThatThrownBy(() -> tradeService.buy(car.getId(), "인증실패"))
                .isInstanceOf(BuyerVerificationException.class);

        assertThat(tradeMapper.countByCarId(car.getId())).isZero();
        assertThat(carService.findCar(car.getId()).getStatus()).isEqualTo(CarStatus.SALE);
    }
}

