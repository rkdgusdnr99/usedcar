package hello.usedcar.repository.mybatis;

import hello.usedcar.domain.Trade;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TradeMapper {

    void save(Trade trade);

    int countByCarId(Long carId);
}