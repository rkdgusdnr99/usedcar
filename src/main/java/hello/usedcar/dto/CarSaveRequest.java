package hello.usedcar.dto;

import hello.usedcar.domain.Car;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarSaveRequest {

    private String carNumber;
    private String manufacturer;
    private String modelName;
    private Integer modelYear;
    private Integer mileage;
    private Integer price;

    public Car toCar() {
        return new Car(carNumber, manufacturer, modelName, modelYear, mileage, price);
    }
}
