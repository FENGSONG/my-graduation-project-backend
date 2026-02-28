package com.xfs.vehicle.service.impl;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.vehicle.mapper.VehicleMapper;
import com.xfs.vehicle.pojo.dto.VehicleQuery;
import com.xfs.vehicle.pojo.dto.VehicleSaveParam;
import com.xfs.vehicle.pojo.entity.Vehicle;
import com.xfs.vehicle.pojo.vo.VehicleVO;
import com.xfs.vehicle.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VehicleServiceImpl implements VehicleService {
    @Autowired
    VehicleMapper vehicleMapper;

    @Override
    public List<VehicleVO> selectVehicle(VehicleQuery vehicleQuery) {
        log.debug("查询车辆业务，参数：{}", vehicleQuery);
        List<VehicleVO> list = vehicleMapper.selectVehicle(vehicleQuery);
        return list;
    }

    @Override
    public void saveVehicle(VehicleSaveParam vehicleSaveParam) {
        log.debug("保存车辆业务，参数：{}", vehicleSaveParam);
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(vehicleSaveParam, vehicle);
        //判断车牌号是否存在
        VehicleVO vehicleVO = vehicleMapper.selectVehicleByLincense(vehicle.getLicense());
        if(vehicle.getId() == null){//新增
            if(vehicleVO != null){//如果车牌号已存在,说明即将要插入的车牌号重复
                throw new ServiceException(StatusCode.LICENSE_EXISTS);
            }
            vehicle.setStatus("1");//空闲
            vehicle.setGeofenceBindStatus("0");//未绑定围栏
            vehicle.setCreateTime(new Date());
            vehicleMapper.insert(vehicle);
        }else{//更新
            //如果车牌号已存在,且当前传入的车牌号车辆id不是当前要更新的车辆id,说明即将要插入的车牌号重复
            if(vehicleVO != null && vehicleVO.getId() != vehicle.getId()){
                throw new ServiceException(StatusCode.LICENSE_EXISTS);
            }
            vehicle.setUpdateTime(new Date());
            vehicleMapper.update(vehicle);
        }
    }

    @Override
    public void deleteVehicle(Long id) {
        log.debug("删除车辆业务，参数：{}", id);
        vehicleMapper.deleteById(id);
    }

    @Override
    public void unbindVehicle(Long vehicleId) {
        log.debug("解绑车辆业务，参数：{}", vehicleId);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setGeofenceBindStatus("0");
        vehicle.setGeofenceId(null);
        vehicle.setUpdateTime(new Date());
        /* 之前的update SQL要求围栏id不为null才会拼接更新条件
        * 但此处的围栏id就需要设置为null值,所以复用不了,我们可以写一个新的SQL*/
        vehicleMapper.updateNullValue(vehicle);
    }

    @Override
    public void bindVehicle(Long geofenceId, Long vehicleId) {
        log.debug("绑定车辆业务，参数：{}", geofenceId);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setGeofenceBindStatus("1");
        vehicle.setGeofenceId(geofenceId);
        vehicle.setUpdateTime(new Date());
        vehicleMapper.update(vehicle);
    }
}
