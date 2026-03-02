package com.xfs.vehicle.mapper;

import com.xfs.vehicle.pojo.dto.VehicleQuery;
import com.xfs.vehicle.pojo.entity.Vehicle;
import com.xfs.vehicle.pojo.vo.VehicleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date; // 如果你的实体类时间字段用的是 Date，请换成 java.util.Date
import java.util.List;

@Repository
public interface VehicleMapper {

    List<VehicleVO> selectVehicle(VehicleQuery vehicleQuery);

    void insert(Vehicle vehicle);

    void update(Vehicle vehicle);

    VehicleVO selectVehicleByLincense(String license);

    void deleteById(Long id);

    void updateNullValue(Vehicle vehicle);

    /**
     * 根据时间段查询空闲（未被排期占用）的可用车辆
     * @param startTime 预计开始使用时间
     * @param endTime   预计结束使用时间
     * @return 可用车辆列表
     */
    List<VehicleVO> findAvailableVehicles(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}