package com.xfs.vehicleinsurance.mapper;

import com.xfs.vehicleinsurance.pojo.dto.VehicleInsuranceQuery;
import com.xfs.vehicleinsurance.pojo.entity.VehicleInsurance;
import com.xfs.vehicleinsurance.pojo.vo.VehicleInsuranceVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleInsuranceMapper {
    List<VehicleInsuranceVO> selectVehicleInsurance(VehicleInsuranceQuery query);

    VehicleInsuranceVO selectByPolicyNo(String policyNo);

    void insert(VehicleInsurance entity);

    void update(VehicleInsurance entity);

    void deleteById(Long id);
}
