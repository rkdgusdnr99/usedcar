package hello.usedcar.web.api;

import hello.usedcar.domain.Car;
import hello.usedcar.dto.CarSaveRequest;
import hello.usedcar.dto.CarSearchCond;
import hello.usedcar.dto.CarUpdateRequest;
import hello.usedcar.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarApiController {

    private final CarService carService;

    // GET /api/cars?modelName=쏘나타&maxPrice=2500
    @GetMapping
    public List<Car> cars(@ModelAttribute CarSearchCond cond) {
        return carService.findCars(cond);
    }

    // GET /api/cars/1
    @GetMapping("/{carId}")
    public ResponseEntity<Car> car(@PathVariable Long carId) {
        return ResponseEntity.of(carService.findById(carId)); // 있으면 200, 없으면 404
    }

    // POST /api/cars  (JSON 본문)
    @PostMapping
    public ResponseEntity<Car> register(@Valid @RequestBody CarSaveRequest request) {
        Car saved = carService.register(request);
        URI location = URI.create("/api/cars/" + saved.getId());
        return ResponseEntity.created(location).body(saved); // 201 + Location
    }

    // PATCH /api/cars/1  (JSON 본문)
    @PatchMapping("/{carId}")
    public ResponseEntity<Car> update(@PathVariable Long carId,
                                      @Valid @RequestBody CarUpdateRequest request) {
        return ResponseEntity.of(carService.update(carId, request));
    }
}

