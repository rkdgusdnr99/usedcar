package hello.usedcar.service;

import hello.usedcar.domain.Car;
import hello.usedcar.dto.CarSaveRequest;
import hello.usedcar.dto.CarSearchCond;
import hello.usedcar.dto.CarUpdateRequest;
import hello.usedcar.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public Car register(CarSaveRequest request) {
        return carRepository.save(request.toCar());
    }

    public Optional<Car> update(Long carId, CarUpdateRequest request) {
        Optional<Car> car = carRepository.findById(carId);
        car.ifPresent(found -> carRepository.update(carId, request));
        return carRepository.findById(carId);
    }

    public Optional<Car> findById(Long carId) {
        return carRepository.findById(carId);
    }

    public List<Car> findCars(CarSearchCond cond) {
        return carRepository.findAll(cond);
    }

    public boolean isDuplicateCarNumber(String carNumber) {
        return carRepository.findByCarNumber(carNumber).isPresent();
    }
}
