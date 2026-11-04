package hello.usedcar.dto;

import hello.usedcar.domain.Car;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarSaveRequest {

    @NotBlank
    @Pattern(regexp = "^\\d{2,3}[가-힣]\\d{4}$")  // 12가3456, 123가4567
    private String carNumber;

    @NotBlank
    private String manufacturer;

    @NotBlank
    private String modelName;

    @NotNull
    @Range(min = 1990, max = 2026)
    private Integer modelYear;

    @NotNull
    @Min(0)
    private Integer mileage;

    @NotNull
    @Range(min = 10, max = 100000)  // 10만원 ~ 10억
    private Integer price;

    public Car toCar() {
        return new Car(carNumber, manufacturer, modelName, modelYear, mileage, price);
    }
}