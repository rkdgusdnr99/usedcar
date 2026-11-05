package hello.usedcar.repository.mybatis;

import hello.usedcar.domain.Car;
import hello.usedcar.dto.CarSearchCond;
import hello.usedcar.dto.CarUpdateRequest;
import hello.usedcar.dto.MarketPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 인터페이스만 만들면 MyBatis가 구현체를 만들어 스프링 빈으로 등록한다.
 * 실제 SQL은 resources/mapper/CarMapper.xml 에 있다 (namespace + id로 연결).
 */
@Mapper
public interface CarMapper {

    void save(Car car);

    // 파라미터가 2개 이상이면 @Param으로 이름을 붙인다
    void update(@Param("id") Long id, @Param("updateParam") CarUpdateRequest updateParam);

    Optional<Car> findById(Long id);

    Optional<Car> findByCarNumber(String carNumber);

    List<Car> findAll(CarSearchCond cond);

    MarketPrice findMarketPrice(String modelName);
}