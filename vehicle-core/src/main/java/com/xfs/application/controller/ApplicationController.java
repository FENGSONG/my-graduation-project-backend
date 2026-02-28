package com.xfs.application.controller;


import com.xfs.application.pojo.dto.ApplicationQuery;
import com.xfs.base.response.JsonResult;
import com.xfs.application.pojo.dto.ApplicationSaveParam;
import com.xfs.application.pojo.vo.ApplicationVO;
import com.xfs.application.service.ApplicationService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "申请模块")
@Slf4j
@RestController
@RequestMapping("/v1/application")
public class ApplicationController {
    @Autowired
    ApplicationService applicationService;

    @Operation(summary = "新增申请")
    @ApiOperationSupport(order = 10)
    @PostMapping("save")
    public JsonResult saveApplication(
            @Validated ApplicationSaveParam applicationSaveParam){
        log.debug("新增申请参数:{}",applicationSaveParam);
        applicationService.save(applicationSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "查询申请单列表数据")
    @ApiOperationSupport(order = 20)
    @GetMapping("select")
    public JsonResult selectApplication(ApplicationQuery applicationQuery){
        log.debug("查询申请单列表数据参数:{}",applicationQuery);
        List<ApplicationVO> list = applicationService.selectApplication(applicationQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "撤销申请")
    @ApiOperationSupport(order = 30)
    @PostMapping("cancel/{id}")
    public JsonResult cancel(@PathVariable Long id){
        log.debug("撤销申请参数:{}",id);
        applicationService.cancel(id);
        return JsonResult.ok();
    }


    @Operation(summary = "分配车辆")
    @ApiOperationSupport(order = 40)
    @PostMapping("distribute/{applicationId}/{vehicleId}")
    public JsonResult distribute(@PathVariable Long applicationId, @PathVariable Long vehicleId){
        log.debug("分配车辆参数:申请单编号:{},车辆编号:{}",applicationId,vehicleId);
        applicationService.distribute(applicationId,vehicleId);
        return JsonResult.ok();
    }

    @Operation(summary = "还车")
    @ApiOperationSupport(order = 50)
    @PostMapping("back/{applicationId}/{vehicleId}")
    public JsonResult back(@PathVariable Long applicationId, @PathVariable Long vehicleId){
        log.debug("还车参数:申请单编号:{},车辆编号:{}",applicationId,vehicleId);
        applicationService.back(applicationId,vehicleId);
        return JsonResult.ok();
    }















}
