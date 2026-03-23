package com.xfs.vehicleinsurance.service;

import com.xfs.vehicleinsurance.pojo.dto.VehicleInsuranceQuery;
import com.xfs.vehicleinsurance.pojo.dto.VehicleInsuranceSaveParam;
import com.xfs.vehicleinsurance.pojo.vo.VehicleInsuranceVO;

import java.util.List;

public interface VehicleInsuranceService {
    List<VehicleInsuranceVO> selectVehicleInsurance(VehicleInsuranceQuery query);

    void saveVehicleInsurance(VehicleInsuranceSaveParam saveParam);

    void deleteVehicleInsurance(Long id);
}
