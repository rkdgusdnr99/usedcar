package hello.usedcar.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TradeLogMapper {

    void save(@Param("carId") Long carId,
              @Param("buyerName") String buyerName,
              @Param("message") String message);

    int countByCarId(Long carId);
}
