package com.xfs.geofence.controller;

import com.xfs.base.response.JsonResult;
import com.xfs.base.auth.AuthGuard;
import com.xfs.geofence.pojo.dto.GeofenceQuery;
import com.xfs.geofence.pojo.dto.GeofenceSaveParam;
import com.xfs.geofence.pojo.vo.GeofenceVO;
import com.xfs.geofence.service.GeofenceService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "围栏模块")
@Slf4j
@RestController
@RequestMapping("/v1/geofence")
public class GeofenceController {
    @Autowired
    GeofenceService geofenceService;
    @Autowired
    AuthGuard authGuard;

    @Operation(summary = "查询电子围栏")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult selectGeofence(GeofenceQuery geofenceQuery){
        log.debug("查询电子围栏:geofenceQuery={}",geofenceQuery);
        authGuard.requireDispatcher();
        List<GeofenceVO> list = geofenceService.selectGeofence(geofenceQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "修改电子围栏状态")
    @ApiOperationSupport(order = 20)
    @PostMapping("update/{geofenceId}/{status}")
    public JsonResult updateGeofenceStatus(
            @PathVariable Long geofenceId, @PathVariable String status){
        log.debug("修改电子围栏状态:geofenceId={},status={}",geofenceId,status);
        authGuard.requireDispatcher();
        geofenceService.updateStatus(geofenceId,status);
        return JsonResult.ok();
    }

    @Operation(summary = "删除电子围栏")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult deleteGeofence(@PathVariable Long id){
        log.debug("删除电子围栏:id={}",id);
        authGuard.requireDispatcher();
        geofenceService.deleteGeofence(id);
        return JsonResult.ok();
    }

    @Operation(summary = "保存电子围栏")
    @ApiOperationSupport(order = 40)
    @PostMapping("save")
    public JsonResult saveGeofence(@Validated @RequestBody GeofenceSaveParam geofenceSaveParam){
        log.debug("保存电子围栏:geofenceSaveParam={}",geofenceSaveParam);
        authGuard.requireDispatcher();
        geofenceService.saveGeofence(geofenceSaveParam);
        return JsonResult.ok();
    }

}
