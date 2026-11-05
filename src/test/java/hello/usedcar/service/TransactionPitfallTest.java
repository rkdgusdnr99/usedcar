package hello.usedcar.service;

import hello.usedcar.domain.Trade;
import hello.usedcar.repository.mybatis.TradeMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 실무에서 자주 터지는 트랜잭션 함정 두 가지를 직접 확인한다.
 */
@SpringBootTest
class TransactionPitfallTest {

    @Autowired CheckedExceptionService checkedExceptionService;
    @Autowired InternalCallService internalCallService;
    @Autowired TradeMapper tradeMapper;

    @Test
    @DisplayName("함정 1: rollbackFor 없이 체크 예외가 나면 롤백되지 않고 커밋된다")
    void checkedExceptionCommits() {
        long carId = 5L; // data.sql의 G80
        int before = tradeMapper.countByCarId(carId);

        assertThatThrownBy(() -> checkedExceptionService.saveThenThrowChecked(carId))
                .isInstanceOf(Exception.class);

        assertThat(tradeMapper.countByCarId(carId)).isEqualTo(before + 1); // 예외가 났는데 저장돼 있다!
    }

    @Test
    @DisplayName("함정 2: 같은 클래스 안에서 부르면 @Transactional이 안 걸린다")
    void internalCall() {
        internalCallService.internal();                 // 밖에서 직접 호출 → 프록시를 거침
        assertThat(internalCallService.isTxActive()).isTrue();

        internalCallService.external();                 // external() 안에서 this.internal() 호출
        assertThat(internalCallService.isTxActive()).isFalse(); // 프록시를 안 거쳐서 트랜잭션 없음
    }

    @TestConfiguration
    static class Config {
        @Bean
        CheckedExceptionService checkedExceptionService(TradeMapper tradeMapper) {
            return new CheckedExceptionService(tradeMapper);
        }

        @Bean
        InternalCallService internalCallService() {
            return new InternalCallService();
        }
    }

    @RequiredArgsConstructor
    static class CheckedExceptionService {
        private final TradeMapper tradeMapper;

        @Transactional // rollbackFor 없음 = 기본 규칙
        public void saveThenThrowChecked(Long carId) throws Exception {
            tradeMapper.save(new Trade(carId, "체크예외", 3900));
            throw new Exception("체크 예외");
        }
    }

    static class InternalCallService {
        private boolean txActive;

        // 필드를 직접 읽으면 프록시 객체의 (빈) 필드를 읽게 된다 → 반드시 메서드로 읽는다
        public boolean isTxActive() {
            return txActive;
        }

        public void external() {
            internal(); // = this.internal()
        }

        @Transactional
        public void internal() {
            txActive = TransactionSynchronizationManager.isActualTransactionActive();
        }
    }
}