package com.xfs.vehicleinspection.mapper;

import com.xfs.vehicleinspection.pojo.dto.VehicleInspectionQuery;
import com.xfs.vehicleinspection.pojo.entity.VehicleInspection;
import com.xfs.vehicleinspection.pojo.vo.VehicleInspectionVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleInspectionMapper {
    List<VehicleInspectionVO> selectVehicleInspection(VehicleInspectionQuery query);

    VehicleInspectionVO selectByCertNo(String certNo);

    void insert(VehicleInspection entity);

    void update(VehicleInspection entity);

    void deleteById(Long id);
}
