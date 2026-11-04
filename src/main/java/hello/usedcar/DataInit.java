package hello.usedcar;

import hello.usedcar.dto.CarSaveRequest;
import hello.usedcar.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 화면을 바로 둘러볼 수 있게 샘플 차량을 넣는다.
 * local 프로필에서만 동작한다 (테스트에서는 안 돈다).
 */
@Profile("local")
@Component
@RequiredArgsConstructor
public class DataInit {

    private final CarService carService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        carService.register(new CarSaveRequest("12가3456", "현대", "쏘나타", 2021, 42000, 2150));
        carService.register(new CarSaveRequest("34나5678", "현대", "쏘나타", 2019, 78000, 1590));
        carService.register(new CarSaveRequest("56다7890", "기아", "K5", 2022, 21000, 2480));
        carService.register(new CarSaveRequest("78라1234", "제네시스", "G80", 2020, 55000, 3900));
    }
}