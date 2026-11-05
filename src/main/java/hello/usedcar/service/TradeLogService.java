package hello.usedcar.service;

import hello.usedcar.repository.mybatis.TradeLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 매매 시도 기록.
 * REQUIRES_NEW: 바깥 트랜잭션(구매)과 상관없이 새 트랜잭션을 열고 바로 커밋한다.
 * → 구매가 실패해서 롤백되어도 "시도했다"는 기록은 남는다.
 */
@Service
@RequiredArgsConstructor
public class TradeLogService {

    private final TradeLogMapper tradeLogMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(Long carId, String buyerName, String message) {
        tradeLogMapper.save(carId, buyerName, message);
    }
}