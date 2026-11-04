package hello.usedcar.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Car {

    private Long id;
    private String carNumber;     // 차량번호 (예: 12가3456)
    private String manufacturer;  // 제조사
    private String modelName;     // 모델명
    private Integer modelYear;    // 연식
    private Integer mileage;      // 주행거리 (km)
    private Integer price;        // 판매가 (만원)
    private CarStatus status;     // 판매 상태

    public Car(String carNumber, String manufacturer, String modelName,
               Integer modelYear, Integer mileage, Integer price) {
        this.carNumber = carNumber;
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        this.modelYear = modelYear;
        this.mileage = mileage;
        this.price = price;
        this.status = CarStatus.SALE;
    }
}
