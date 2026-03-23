package com.xfs.vehicleinsurance.service.impl;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.vehicle.mapper.VehicleMapper;
import com.xfs.vehicle.pojo.dto.VehicleQuery;
import com.xfs.vehicle.pojo.vo.VehicleVO;
import com.xfs.vehicleinsurance.mapper.VehicleInsuranceMapper;
import com.xfs.vehicleinsurance.pojo.dto.VehicleInsuranceQuery;
import com.xfs.vehicleinsurance.pojo.dto.VehicleInsuranceSaveParam;
import com.xfs.vehicleinsurance.pojo.entity.VehicleInsurance;
import com.xfs.vehicleinsurance.pojo.vo.VehicleInsuranceVO;
import com.xfs.vehicleinsurance.service.VehicleInsuranceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class VehicleInsuranceServiceImpl implements VehicleInsuranceService {
    @Autowired
    private VehicleInsuranceMapper insuranceMapper;
    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public List<VehicleInsuranceVO> selectVehicleInsurance(VehicleInsuranceQuery query) {
        log.debug("查询车辆保险业务，参数：{}", query);
        List<VehicleInsuranceVO> list = insuranceMapper.selectVehicleInsurance(query);
        Date now = new Date();
        for (VehicleInsuranceVO item : list) {
            if (item == null) {
                continue;
            }
            item.setStatus(calculateStatus(item.getEndTime(), now));
        }
        return list;
    }

    @Override
    public void saveVehicleInsurance(VehicleInsuranceSaveParam saveParam) {
        log.debug("保存车辆保险业务，参数：{}", saveParam);
        ensureVehicleExists(saveParam.getVehicleId());

        VehicleInsuranceVO existed = insuranceMapper.selectByPolicyNo(saveParam.getPolicyNo());
        if (saveParam.getId() == null) {
            if (existed != null) {
                throw new ServiceException(StatusCode.OPERATION_FAILED);
            }
        } else if (existed != null && !Objects.equals(existed.getId(), saveParam.getId())) {
            throw new ServiceException(StatusCode.OPERATION_FAILED);
        }

        VehicleInsurance entity = new VehicleInsurance();
        BeanUtils.copyProperties(saveParam, entity);
        entity.setStatus(calculateStatus(entity.getEndTime(), new Date()));

        if (entity.getId() == null) {
            entity.setCreateTime(new Date());
            insuranceMapper.insert(entity);
        } else {
            entity.setUpdateTime(new Date());
            insuranceMapper.update(entity);
        }
    }

    @Override
    public void deleteVehicleInsurance(Long id) {
        log.debug("删除车辆保险业务，参数：{}", id);
        insuranceMapper.deleteById(id);
    }

    private String calculateStatus(Date endTime, Date now) {
        if (endTime == null) {
            return "10";
        }
        return endTime.before(now) ? "20" : "10";
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
