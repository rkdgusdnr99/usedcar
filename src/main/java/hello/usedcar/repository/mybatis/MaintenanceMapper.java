package hello.usedcar.repository.mybatis;

import hello.usedcar.domain.Maintenance;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MaintenanceMapper {

    List<Maintenance> findByCarId(Long carId);
}