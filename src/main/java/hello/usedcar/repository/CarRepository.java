package hello.usedcar.repository;

import hello.usedcar.domain.Car;
import hello.usedcar.dto.CarSearchCond;
import hello.usedcar.dto.CarUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface CarRepository {

    Car save(Car car);

    void update(Long id, CarUpdateRequest updateParam);

    Optional<Car> findById(Long id);

    Optional<Car> findByCarNumber(String carNumber);

    List<Car> findAll(CarSearchCond cond);

    // 판매중(SALE)일 때만 판매완료(SOLD)로 바꾼다. 바뀜 행 수를 돌려준다 (0이면 이미 팔린 차)
    int markSold(Long id);
}
