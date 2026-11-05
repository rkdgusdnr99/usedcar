package hello.usedcar.service;

import hello.usedcar.domain.Car;
import hello.usedcar.domain.Maintenance;
import hello.usedcar.dto.CarSaveRequest;
import hello.usedcar.dto.CarSearchCond;
import hello.usedcar.dto.CarUpdateRequest;
import hello.usedcar.dto.MarketPrice;
import hello.usedcar.exception.CarNotFoundException;
import hello.usedcar.exception.DuplicateCarNumberException;
import hello.usedcar.repository.CarRepository;
import hello.usedcar.repository.mybatis.CarMapper;
import hello.usedcar.repository.mybatis.MaintenanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;                  // 조회 전용 쿼리는 매퍼를 바로 쓴다
    private final MaintenanceMapper maintenanceMapper;

    public Car register(CarSaveRequest request) {
        if (isDuplicateCarNumber(request.getCarNumber())) {
            throw new DuplicateCarNumberException(request.getCarNumber());
        }
        return carRepository.save(request.toCar());
    }

    public Car update(Long carId, CarUpdateRequest request) {
        findCar(carId); // 없으면 여기서 예외
        carRepository.update(carId, request);
        return findCar(carId);
    }

    public Car findCar(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));
    }

    public List<Car> findCars(CarSearchCond cond) {
        return carRepository.findAll(cond);
    }

    public boolean isDuplicateCarNumber(String carNumber) {
        return carRepository.findByCarNumber(carNumber).isPresent();
    }

    public List<Maintenance> findMaintenances(Long carId) {
        findCar(carId);
        return maintenanceMapper.findByCarId(carId);
    }

    public MarketPrice getMarketPrice(String modelName) {
        return carMapper.findMarketPrice(modelName);
    }
}