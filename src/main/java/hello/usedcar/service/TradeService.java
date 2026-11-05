package hello.usedcar.service;

import hello.usedcar.domain.Car;
import hello.usedcar.domain.CarStatus;
import hello.usedcar.domain.Trade;
import hello.usedcar.exception.AlreadySoldException;
import hello.usedcar.exception.BuyerVerificationException;
import hello.usedcar.exception.CarNotFoundException;
import hello.usedcar.repository.CarRepository;
import hello.usedcar.repository.mybatis.TradeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    private final CarRepository carRepository;
    private final TradeMapper tradeMapper;
    private final TradeLogService tradeLogService;  // 9강: 다른 클래스여야 프록시를 거친다 (8강 내부 호출 함정)

    /**
     * 구매: ① 거래 저장 → ② 구매자 확인 → ③ 차량을 판매완료로.
     * 셋은 전부 성공하거나 전부 취소되어야 한다 → 하나의 트랜잭션.
     */
    @Transactional(rollbackFor = BuyerVerificationException.class)
    public Trade buy(Long carId, String buyerName) throws BuyerVerificationException {
        log.info("트랜잭션 활성={}", TransactionSynchronizationManager.isActualTransactionActive());
        tradeLogService.log(carId, buyerName, "구매 시도");  // 별도 트랜잭션으로 바로 커밋 (9강)

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));
        if (car.getStatus() == CarStatus.SOLD) {
            throw new AlreadySoldException(carId);
        }

        Trade trade = new Trade(carId, buyerName, car.getPrice());
        tradeMapper.save(trade);                        // ① 거래 저장

        verifyBuyer(buyerName);                         // ② 여기서 실패하면 ①도 취소되어야 한다

        if (carRepository.markSold(carId) == 0) {       // ③ 판매완료 (그 사이 누가 먼저 샀으면 0)
            throw new AlreadySoldException(carId);
        }
        return trade;
    }

    // 실습용: 구매자 이름으로 실패 상황을 흉내낸다
    private void verifyBuyer(String buyerName) throws BuyerVerificationException {
        if (buyerName.contains("시스템오류")) {
            throw new IllegalStateException("결제 시스템 오류");          // 런타임 예외
        }
        if (buyerName.contains("인증실패")) {
            throw new BuyerVerificationException("구매자 본인인증 실패");  // 체크 예외
        }
    }
}