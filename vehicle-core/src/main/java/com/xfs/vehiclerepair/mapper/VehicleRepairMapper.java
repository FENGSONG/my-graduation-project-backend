package com.xfs.vehiclerepair.mapper;

import com.xfs.vehiclerepair.pojo.dto.VehicleRepairQuery;
import com.xfs.vehiclerepair.pojo.entity.VehicleRepair;
import com.xfs.vehiclerepair.pojo.vo.VehicleRepairVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepairMapper {
    List<VehicleRepairVO> selectVehicleRepair(VehicleRepairQuery query);

    void insert(VehicleRepair entity);

    void update(VehicleRepair entity);

    void deleteById(Long id);
}
