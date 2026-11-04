package hello.usedcar.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarUpdateRequest {

    @NotNull
    @Min(0)
    private Integer mileage;

    @NotNull
    @Range(min = 10, max = 100000)
    private Integer price;
}

