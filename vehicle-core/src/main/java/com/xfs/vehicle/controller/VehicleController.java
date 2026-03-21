package com.xfs.vehicle.controller;

import com.xfs.base.response.JsonResult;
import com.xfs.base.auth.AuthGuard;
import com.xfs.vehicle.pojo.dto.VehicleQuery;
import com.xfs.vehicle.pojo.dto.VehicleSaveParam;
import com.xfs.vehicle.pojo.vo.VehicleVO;
import com.xfs.vehicle.service.VehicleService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "车辆模块")
@Slf4j
@RestController
@RequestMapping("/v1/vehicle")
public class VehicleController {
    @Autowired
    VehicleService vehicleService;
    @Autowired
    AuthGuard authGuard;

    @Operation(summary = "查询车辆")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult selectVehicle(VehicleQuery vehicleQuery){
        log.debug("查询车辆:vehicleQuery={}",vehicleQuery);
        authGuard.requireLoginUser();
        List<VehicleVO> list = vehicleService.selectVehicle(vehicleQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "保存车辆")
    @ApiOperationSupport(order = 20)
    @PostMapping("save")
    public JsonResult saveVehicle(@Validated VehicleSaveParam vehicleSaveParam){
        log.debug("保存车辆:vehicleSaveParam={}",vehicleSaveParam);
        authGuard.requireDispatcher();
        vehicleService.saveVehicle(vehicleSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除车辆")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult deleteVehicle(@PathVariable Long id){
        log.debug("删除车辆:id={}",id);
        authGuard.requireDispatcher();
        vehicleService.deleteVehicle(id);
        return JsonResult.ok();
    }

    @Operation(summary = "解绑车辆")
    @ApiOperationSupport(order = 40)
    @PostMapping("unbind/{vehicleId}")
    public JsonResult unbindVehicle(@PathVariable Long vehicleId){
        log.debug("解绑车辆:vehicleId={}",vehicleId);
        authGuard.requireDispatcher();
        vehicleService.unbindVehicle(vehicleId);
        return JsonResult.ok();
    }

    @Operation(summary = "绑定车辆")
    @ApiOperationSupport(order = 50)
    @PostMapping("bind/{geofenceId}/{vehicleId}")
    public JsonResult bindVehicle(
            @PathVariable Long geofenceId,@PathVariable Long vehicleId){
        log.debug("绑定车辆:geofenceId={},vehicleId={}",geofenceId,vehicleId);
        authGuard.requireDispatcher();
        vehicleService.bindVehicle(geofenceId,vehicleId);
        return JsonResult.ok();
    }
}
