package com.xfs.vehicle.service;

import com.xfs.vehicle.pojo.dto.VehicleQuery;
import com.xfs.vehicle.pojo.dto.VehicleSaveParam;
import com.xfs.vehicle.pojo.vo.VehicleVO;

import java.util.List;

public interface VehicleService {
    List<VehicleVO> selectVehicle(VehicleQuery vehicleQuery);

    void saveVehicle(VehicleSaveParam vehicleSaveParam);

    void deleteVehicle(Long id);

    void unbindVehicle(Long vehicleId);

    void bindVehicle(Long geofenceId, Long vehicleId);
}
