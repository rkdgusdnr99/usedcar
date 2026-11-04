package hello.usedcar.repository;

import hello.usedcar.domain.Car;
import hello.usedcar.dto.CarSearchCond;
import hello.usedcar.dto.CarUpdateRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryCarRepository implements CarRepository {

    private final Map<Long, Car> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public Car save(Car car) {
        car.setId(sequence.incrementAndGet());
        store.put(car.getId(), car);
        return car;
    }

    @Override
    public void update(Long id, CarUpdateRequest updateParam) {
        Car car = store.get(id);
        car.setMileage(updateParam.getMileage());
        car.setPrice(updateParam.getPrice());
    }

    @Override
    public Optional<Car> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Car> findByCarNumber(String carNumber) {
        return store.values().stream()
                .filter(car -> car.getCarNumber().equals(carNumber))
                .findFirst();
    }

    @Override
    public List<Car> findAll(CarSearchCond cond) {
        return store.values().stream()
                .filter(car -> !StringUtils.hasText(cond.getModelName())
                        || car.getModelName().contains(cond.getModelName()))
                .filter(car -> cond.getMaxPrice() == null
                        || car.getPrice() <= cond.getMaxPrice())
                .filter(car -> cond.getStatus() == null
                        || car.getStatus() == cond.getStatus())
                .toList();
    }
}
