package hello.usedcar.web.api;

import hello.usedcar.domain.Trade;
import hello.usedcar.dto.TradeRequest;
import hello.usedcar.exception.BuyerVerificationException;
import hello.usedcar.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TradeApiController {

    private final TradeService tradeService;

    // POST /api/cars/1/trades  {"buyerName": "홍길동"}
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/cars/{carId}/trades")
    public Trade buy(@PathVariable Long carId,
                     @Valid @RequestBody TradeRequest request) throws BuyerVerificationException {
        return tradeService.buy(carId, request.getBuyerName());
    }
}