package com.xfs.vehicleinspection.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.xfs.base.auth.AuthGuard;
import com.xfs.base.response.JsonResult;
import com.xfs.vehicleinspection.pojo.dto.VehicleInspectionQuery;
import com.xfs.vehicleinspection.pojo.dto.VehicleInspectionSaveParam;
import com.xfs.vehicleinspection.pojo.vo.VehicleInspectionVO;
import com.xfs.vehicleinspection.service.VehicleInspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "车辆年检模块")
@Slf4j
@RestController
@RequestMapping("/v1/vehicle-inspection")
public class VehicleInspectionController {
    @Autowired
    private VehicleInspectionService inspectionService;
    @Autowired
    private AuthGuard authGuard;

    @Operation(summary = "查询车辆年检")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult select(VehicleInspectionQuery query) {
        log.debug("查询车辆年检参数:{}", query);
        authGuard.requireLoginUser();
        List<VehicleInspectionVO> list = inspectionService.selectVehicleInspection(query);
        return JsonResult.ok(list);
    }

    @Operation(summary = "保存车辆年检")
    @ApiOperationSupport(order = 20)
    @PostMapping("save")
    public JsonResult save(@Validated VehicleInspectionSaveParam saveParam) {
        log.debug("保存车辆年检参数:{}", saveParam);
        authGuard.requireDispatcher();
        inspectionService.saveVehicleInspection(saveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除车辆年检")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult delete(@PathVariable Long id) {
        log.debug("删除车辆年检参数:{}", id);
        authGuard.requireDispatcher();
        inspectionService.deleteVehicleInspection(id);
        return JsonResult.ok();
    }
}
