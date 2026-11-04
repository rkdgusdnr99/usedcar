package hello.usedcar.dto;

import hello.usedcar.domain.CarStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarSearchCond {

    private String modelName;  // 모델명 (부분 일치)
    private Integer maxPrice;  // 최대 가격 (만원)
    private CarStatus status;  // 판매 상태
}
