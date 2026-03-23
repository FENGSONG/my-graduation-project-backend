package com.xfs.vehiclerepair.service;

import com.xfs.vehiclerepair.pojo.dto.VehicleRepairQuery;
import com.xfs.vehiclerepair.pojo.dto.VehicleRepairSaveParam;
import com.xfs.vehiclerepair.pojo.vo.VehicleRepairVO;

import java.util.List;

public interface VehicleRepairService {
    List<VehicleRepairVO> selectVehicleRepair(VehicleRepairQuery query);

    void saveVehicleRepair(VehicleRepairSaveParam saveParam);

    void deleteVehicleRepair(Long id);
}
