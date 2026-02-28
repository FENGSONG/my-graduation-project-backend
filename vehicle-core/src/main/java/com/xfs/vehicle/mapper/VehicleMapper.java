package com.xfs.vehicle.mapper;

import com.xfs.vehicle.pojo.dto.VehicleQuery;
import com.xfs.vehicle.pojo.entity.Vehicle;
import com.xfs.vehicle.pojo.vo.VehicleVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleMapper {
    List<VehicleVO> selectVehicle(VehicleQuery vehicleQuery);

    void insert(Vehicle vehicle);

    void update(Vehicle vehicle);

    VehicleVO selectVehicleByLincense(String license);

    void deleteById(Long id);

    void updateNullValue(Vehicle vehicle);
}
