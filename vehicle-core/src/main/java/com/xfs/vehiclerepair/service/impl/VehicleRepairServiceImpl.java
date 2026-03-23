package com.xfs.vehiclerepair.service.impl;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.vehicle.mapper.VehicleMapper;
import com.xfs.vehicle.pojo.dto.VehicleQuery;
import com.xfs.vehicle.pojo.vo.VehicleVO;
import com.xfs.vehiclerepair.mapper.VehicleRepairMapper;
import com.xfs.vehiclerepair.pojo.dto.VehicleRepairQuery;
import com.xfs.vehiclerepair.pojo.dto.VehicleRepairSaveParam;
import com.xfs.vehiclerepair.pojo.entity.VehicleRepair;
import com.xfs.vehiclerepair.pojo.vo.VehicleRepairVO;
import com.xfs.vehiclerepair.service.VehicleRepairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VehicleRepairServiceImpl implements VehicleRepairService {
    @Autowired
    private VehicleRepairMapper repairMapper;
    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public List<VehicleRepairVO> selectVehicleRepair(VehicleRepairQuery query) {
        log.debug("查询车辆维修业务，参数：{}", query);
        return repairMapper.selectVehicleRepair(query);
    }

    @Override
    public void saveVehicleRepair(VehicleRepairSaveParam saveParam) {
        log.debug("保存车辆维修业务，参数：{}", saveParam);
        ensureVehicleExists(saveParam.getVehicleId());

        VehicleRepair entity = new VehicleRepair();
        BeanUtils.copyProperties(saveParam, entity);

        if (entity.getId() == null) {
            entity.setCreateTime(new Date());
            repairMapper.insert(entity);
        } else {
            entity.setUpdateTime(new Date());
            repairMapper.update(entity);
        }
    }

    @Override
    public void deleteVehicleRepair(Long id) {
        log.debug("删除车辆维修业务，参数：{}", id);
        repairMapper.deleteById(id);
    }

    private void ensureVehicleExists(Long vehicleId) {
        if (vehicleId == null || vehicleId <= 0) {
            throw new ServiceException(StatusCode.VALIDATE_ERROR);
        }
        VehicleQuery query = new VehicleQuery();
        query.setId(vehicleId);
        List<VehicleVO> vehicleList = vehicleMapper.selectVehicle(query);
        if (vehicleList == null || vehicleList.isEmpty()) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
    }
}
