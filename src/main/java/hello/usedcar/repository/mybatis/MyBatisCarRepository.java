package hello.usedcar.repository.mybatis;

import hello.usedcar.domain.Car;
import hello.usedcar.dto.CarSearchCond;
import hello.usedcar.dto.CarUpdateRequest;
import hello.usedcar.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CarRepository 인터페이스의 MyBatis 구현. 실제 일은 CarMapper에 위임한다.
 * 2강의 MemoryCarRepository를 이걸로 바꿔 끼우는 것만으로 저장소가 DB가 된다.
 */
@Repository
@RequiredArgsConstructor
public class MyBatisCarRepository implements CarRepository {

    private final CarMapper carMapper;

    @Override
    public Car save(Car car) {
        carMapper.save(car);  // 실행 후 car.id가 채워져 있다
        return car;
    }

    @Override
    public void update(Long id, CarUpdateRequest updateParam) {
        carMapper.update(id, updateParam);
    }

    @Override
    public Optional<Car> findById(Long id) {
        return carMapper.findById(id);
    }

    @Override
    public Optional<Car> findByCarNumber(String carNumber) {
        return carMapper.findByCarNumber(carNumber);
    }

    @Override
    public List<Car> findAll(CarSearchCond cond) {
        return carMapper.findAll(cond);
    }
}