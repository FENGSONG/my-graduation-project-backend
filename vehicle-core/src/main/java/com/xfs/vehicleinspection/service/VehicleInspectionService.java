package com.xfs.vehicleinspection.service;

import com.xfs.vehicleinspection.pojo.dto.VehicleInspectionQuery;
import com.xfs.vehicleinspection.pojo.dto.VehicleInspectionSaveParam;
import com.xfs.vehicleinspection.pojo.vo.VehicleInspectionVO;

import java.util.List;

public interface VehicleInspectionService {
    List<VehicleInspectionVO> selectVehicleInspection(VehicleInspectionQuery query);

    void saveVehicleInspection(VehicleInspectionSaveParam saveParam);

    void deleteVehicleInspection(Long id);
}
