package com.xfs.vehicleinspection.service.impl;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.vehicle.mapper.VehicleMapper;
import com.xfs.vehicle.pojo.dto.VehicleQuery;
import com.xfs.vehicle.pojo.vo.VehicleVO;
import com.xfs.vehicleinspection.mapper.VehicleInspectionMapper;
import com.xfs.vehicleinspection.pojo.dto.VehicleInspectionQuery;
import com.xfs.vehicleinspection.pojo.dto.VehicleInspectionSaveParam;
import com.xfs.vehicleinspection.pojo.entity.VehicleInspection;
import com.xfs.vehicleinspection.pojo.vo.VehicleInspectionVO;
import com.xfs.vehicleinspection.service.VehicleInspectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class VehicleInspectionServiceImpl implements VehicleInspectionService {
    @Autowired
    private VehicleInspectionMapper inspectionMapper;
    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public List<VehicleInspectionVO> selectVehicleInspection(VehicleInspectionQuery query) {
        log.debug("查询车辆年检业务，参数：{}", query);
        return inspectionMapper.selectVehicleInspection(query);
    }

    @Override
    public void saveVehicleInspection(VehicleInspectionSaveParam saveParam) {
        log.debug("保存车辆年检业务，参数：{}", saveParam);
        ensureVehicleExists(saveParam.getVehicleId());

        VehicleInspectionVO existed = inspectionMapper.selectByCertNo(saveParam.getCertNo());
        if (saveParam.getId() == null) {
            if (existed != null) {
                throw new ServiceException(StatusCode.OPERATION_FAILED);
            }
        } else if (existed != null && !Objects.equals(existed.getId(), saveParam.getId())) {
            throw new ServiceException(StatusCode.OPERATION_FAILED);
        }

        VehicleInspection entity = new VehicleInspection();
        BeanUtils.copyProperties(saveParam, entity);

        if (entity.getId() == null) {
            entity.setCreateTime(new Date());
            inspectionMapper.insert(entity);
        } else {
            entity.setUpdateTime(new Date());
            inspectionMapper.update(entity);
        }
    }

    @Override
    public void deleteVehicleInspection(Long id) {
        log.debug("删除车辆年检业务，参数：{}", id);
        inspectionMapper.deleteById(id);
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
