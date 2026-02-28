package com.xfs.geofence.service.impl;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.geofence.mapper.GeofenceMapper;
import com.xfs.geofence.pojo.dto.GeofenceQuery;
import com.xfs.geofence.pojo.dto.GeofenceSaveParam;
import com.xfs.geofence.pojo.entity.Geofence;
import com.xfs.geofence.pojo.vo.GeofenceVO;
import com.xfs.geofence.service.GeofenceService;
import com.xfs.vehicle.mapper.VehicleMapper;
import com.xfs.vehicle.pojo.dto.VehicleQuery;
import com.xfs.vehicle.pojo.vo.VehicleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class GeofenceServiceImpl implements GeofenceService {
    @Autowired
    GeofenceMapper geofenceMapper;
    @Autowired
    VehicleMapper vehicleMapper;

    @Override
    public List<GeofenceVO> selectGeofence(GeofenceQuery geofenceQuery) {
        log.debug("查询电子围栏业务，参数：{}", geofenceQuery);
        List<GeofenceVO> list = geofenceMapper.selectGeofence(geofenceQuery);
        //遍历每一个围栏VO,为其补全数据
        for(int i = 0; i < list.size(); i++){
            GeofenceVO geofenceVO = list.get(i);
            //封装车辆的查询条件
            VehicleQuery vehicleQuery = new VehicleQuery();
            vehicleQuery.setGeofenceId(geofenceVO.getId());
            List<VehicleVO> vehicleList = vehicleMapper.selectVehicle(vehicleQuery);
            //获取绑定在当前围栏上的车辆总数
            geofenceVO.setTotalNum(vehicleList.size());
            //定义变量用来保存围栏上的可用车辆数
            int availableNum = 0;
            //遍历当前围栏下的所有车辆
            for(VehicleVO vehicleVO : vehicleList){
                if(vehicleVO.getStatus().equals("1")){
                    availableNum++;
                }
            }
            geofenceVO.setAvailableNum(availableNum);
            geofenceVO.setVehicleList(vehicleList);
        }
        return list;
    }

    @Override
    public void updateStatus(Long geofenceId, String status) {
        log.debug("修改电子围栏状态业务:geofenceId={},status={}",geofenceId,status);
        Geofence geofence = new Geofence();
        geofence.setId(geofenceId);
        geofence.setStatus(status);
        geofence.setUpdateTime(new Date());
        geofenceMapper.update(geofence);
    }

    @Override
    public void deleteGeofence(Long id) {
        log.debug("删除电子围栏业务:id={}",id);
        /* 删除时一定要根据业务设计方案:围栏上有关联的车辆数据 */
        //1.封装车辆的查询条件
        VehicleQuery vehicleQuery = new VehicleQuery();
        vehicleQuery.setGeofenceId(id);
        //2.调用车辆模块的查车方法
        List<VehicleVO> list = vehicleMapper.selectVehicle(vehicleQuery);
        //3.如果该围栏下有绑定的车辆,无法删除,抛出异常
        if(list != null && list.size() > 0){
            throw new ServiceException(StatusCode.VEHICLE_EXISTS);
        }else{
            //4.如果没有绑定的车辆,可以删除
            geofenceMapper.deleteById(id);
        }

    }

    @Override
    public void saveGeofence(GeofenceSaveParam geofenceSaveParam) {
        log.debug("保存电子围栏业务:geofenceSaveParam={}",geofenceSaveParam);
        Geofence geofence = new Geofence();
        BeanUtils.copyProperties(geofenceSaveParam,geofence);
        geofence.setStatus("1");//启用
        geofence.setCreateTime(new Date());
        geofenceMapper.insert(geofence);
    }
}
