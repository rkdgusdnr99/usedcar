package hello.usedcar.web.api;

import hello.usedcar.dto.MarketPrice;
import hello.usedcar.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market-prices")
@RequiredArgsConstructor
public class MarketPriceApiController {

    private final CarService carService;

    // GET /api/market-prices?modelName=쏘나타
    @GetMapping
    public MarketPrice marketPrice(@RequestParam String modelName) {
        return carService.getMarketPrice(modelName);
    }
}