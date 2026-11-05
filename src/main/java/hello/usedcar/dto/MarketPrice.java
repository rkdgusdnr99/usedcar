package hello.usedcar.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 모델별 시세 (판매중인 차량 기준). 집계 쿼리 결과를 담는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class MarketPrice {

    private String modelName;
    private Integer carCount;  // 판매중 대수
    private Integer avgPrice;  // 평균가 (만원)
    private Integer minPrice;
    private Integer maxPrice;
}