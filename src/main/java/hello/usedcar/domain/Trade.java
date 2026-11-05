package hello.usedcar.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Trade {

    private Long id;
    private Long carId;
    private String buyerName;
    private Integer price;          // 거래 가격 (만원)
    private LocalDateTime tradedAt;

    public Trade(Long carId, String buyerName, Integer price) {
        this.carId = carId;
        this.buyerName = buyerName;
        this.price = price;
        this.tradedAt = LocalDateTime.now();
    }
}