package hello.usedcar.repository;

import hello.usedcar.domain.Car;
import hello.usedcar.domain.CarStatus;
import hello.usedcar.dto.CarSearchCond;
import hello.usedcar.dto.CarUpdateRequest;
import hello.usedcar.dto.MarketPrice;
import hello.usedcar.repository.mybatis.CarMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @Transactional: 각 테스트가 끝나면 롤백 → 테스트끼리 데이터가 섞이지 않는다.
 */
@SpringBootTest
@Transactional
class MyBatisCarRepositoryTest {

    @Autowired
    CarRepository carRepository;

    @Autowired
    CarMapper carMapper;

    @Test
    @DisplayName("주입된 구현체는 MyBatisCarRepository다")
    void implementation() {
        assertThat(carRepository).isInstanceOf(hello.usedcar.repository.mybatis.MyBatisCarRepository.class);
    }

    @Test
    @DisplayName("저장하면 DB가 만든 id가 채워지고, 다시 조회할 수 있다")
    void saveAndFind() {
        Car car = carRepository.save(new Car("90가9090", "기아", "셀토스", 2023, 8000, 2600));

        assertThat(car.getId()).isNotNull();
        Car found = carRepository.findById(car.getId()).orElseThrow();
        assertThat(found.getCarNumber()).isEqualTo("90가9090");
        assertThat(found.getStatus()).isEqualTo(CarStatus.SALE); // enum ↔ 문자열 자동 변환
    }

    @Test
    @DisplayName("update는 주행거리와 가격만 바꾼다")
    void update() {
        Car car = carRepository.save(new Car("91나9191", "기아", "셀토스", 2023, 8000, 2600));

        carRepository.update(car.getId(), new CarUpdateRequest(12000, 2450));

        Car found = carRepository.findById(car.getId()).orElseThrow();
        assertThat(found.getMileage()).isEqualTo(12000);
        assertThat(found.getPrice()).isEqualTo(2450);
        assertThat(found.getModelName()).isEqualTo("셀토스");
    }

    @Test
    @DisplayName("동적 쿼리: 조건이 있는 것만 where에 붙는다")
    void dynamicSearch() {
        carRepository.save(new Car("92다9292", "기아", "모하비", 2020, 70000, 3100));
        carRepository.save(new Car("93라9393", "기아", "모하비", 2023, 10000, 4900));

        // 조건 없음 → 전체 (data.sql 5대 + 방금 2대 이상)
        assertThat(carRepository.findAll(new CarSearchCond())).hasSizeGreaterThanOrEqualTo(7);

        // 모델명만
        assertThat(carRepository.findAll(new CarSearchCond("모하비", null, null))).hasSize(2);

        // 모델명 + 최대 가격
        List<Car> cheap = carRepository.findAll(new CarSearchCond("모하비", 4000, null));
        assertThat(cheap).extracting("carNumber").containsExactly("92다9292");

        // 모델명 + 상태
        assertThat(carRepository.findAll(new CarSearchCond("모하비", null, CarStatus.SALE))).hasSize(2);
        assertThat(carRepository.findAll(new CarSearchCond("모하비", null, CarStatus.SOLD))).isEmpty();
    }

    @Test
    @DisplayName("차량번호 중복은 DB UNIQUE 제약이 막고, 스프링이 DuplicateKeyException으로 바꿔준다")
    void uniqueConstraint() {
        carRepository.save(new Car("94마9494", "기아", "레이", 2021, 30000, 1100));

        assertThatThrownBy(() -> carRepository.save(new Car("94마9494", "기아", "레이", 2021, 30000, 1100)))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("시세 집계: 판매중 차량의 대수·평균·최저·최고")
    void marketPrice() {
        carRepository.save(new Car("95바9595", "쉐보레", "트랙스", 2022, 20000, 1800));
        carRepository.save(new Car("96사9696", "쉐보레", "트랙스", 2023, 10000, 2100));
        carRepository.save(new Car("97아9797", "쉐보레", "트랙스", 2024, 5000, 2400));

        MarketPrice price = carMapper.findMarketPrice("트랙스");

        assertThat(price.getCarCount()).isEqualTo(3);
        assertThat(price.getAvgPrice()).isEqualTo(2100);
        assertThat(price.getMinPrice()).isEqualTo(1800);
        assertThat(price.getMaxPrice()).isEqualTo(2400);
    }

    @Test
    @DisplayName("해당 모델이 없으면 0대로 나온다")
    void marketPriceEmpty() {
        MarketPrice price = carMapper.findMarketPrice("없는모델");
        assertThat(price.getCarCount()).isZero();
        assertThat(price.getAvgPrice()).isNull();
    }
}