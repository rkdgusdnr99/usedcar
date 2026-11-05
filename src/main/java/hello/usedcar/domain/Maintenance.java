package hello.usedcar.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Maintenance {

    private Long id;
    private Long carId;
    private LocalDate maintainedAt;  // 정비일
    private String description;      // 정비 내용
    private Integer mileage;         // 정비 당시 주행거리
}