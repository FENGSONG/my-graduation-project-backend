package com.xfs.vehiclerepair.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.xfs.base.auth.AuthGuard;
import com.xfs.base.response.JsonResult;
import com.xfs.vehiclerepair.pojo.dto.VehicleRepairQuery;
import com.xfs.vehiclerepair.pojo.dto.VehicleRepairSaveParam;
import com.xfs.vehiclerepair.pojo.vo.VehicleRepairVO;
import com.xfs.vehiclerepair.service.VehicleRepairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "车辆维修模块")
@Slf4j
@RestController
@RequestMapping("/v1/vehicle-repair")
public class VehicleRepairController {
    @Autowired
    private VehicleRepairService repairService;
    @Autowired
    private AuthGuard authGuard;

    @Operation(summary = "查询车辆维修")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult select(VehicleRepairQuery query) {
        log.debug("查询车辆维修参数:{}", query);
        authGuard.requireLoginUser();
        List<VehicleRepairVO> list = repairService.selectVehicleRepair(query);
        return JsonResult.ok(list);
    }

    @Operation(summary = "保存车辆维修")
    @ApiOperationSupport(order = 20)
    @PostMapping("save")
    public JsonResult save(@Validated VehicleRepairSaveParam saveParam) {
        log.debug("保存车辆维修参数:{}", saveParam);
        authGuard.requireDispatcher();
        repairService.saveVehicleRepair(saveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除车辆维修")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult delete(@PathVariable Long id) {
        log.debug("删除车辆维修参数:{}", id);
        authGuard.requireDispatcher();
        repairService.deleteVehicleRepair(id);
        return JsonResult.ok();
    }
}
