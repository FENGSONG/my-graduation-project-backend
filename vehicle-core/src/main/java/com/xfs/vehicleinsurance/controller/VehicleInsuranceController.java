package com.xfs.vehicleinsurance.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.xfs.base.auth.AuthGuard;
import com.xfs.base.response.JsonResult;
import com.xfs.vehicleinsurance.pojo.dto.VehicleInsuranceQuery;
import com.xfs.vehicleinsurance.pojo.dto.VehicleInsuranceSaveParam;
import com.xfs.vehicleinsurance.pojo.vo.VehicleInsuranceVO;
import com.xfs.vehicleinsurance.service.VehicleInsuranceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "车辆保险模块")
@Slf4j
@RestController
@RequestMapping("/v1/vehicle-insurance")
public class VehicleInsuranceController {
    @Autowired
    private VehicleInsuranceService insuranceService;
    @Autowired
    private AuthGuard authGuard;

    @Operation(summary = "查询车辆保险")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult select(VehicleInsuranceQuery query) {
        log.debug("查询车辆保险参数:{}", query);
        authGuard.requireLoginUser();
        List<VehicleInsuranceVO> list = insuranceService.selectVehicleInsurance(query);
        return JsonResult.ok(list);
    }

    @Operation(summary = "保存车辆保险")
    @ApiOperationSupport(order = 20)
    @PostMapping("save")
    public JsonResult save(@Validated VehicleInsuranceSaveParam saveParam) {
        log.debug("保存车辆保险参数:{}", saveParam);
        authGuard.requireDispatcher();
        insuranceService.saveVehicleInsurance(saveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除车辆保险")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult delete(@PathVariable Long id) {
        log.debug("删除车辆保险参数:{}", id);
        authGuard.requireDispatcher();
        insuranceService.deleteVehicleInsurance(id);
        return JsonResult.ok();
    }
}
