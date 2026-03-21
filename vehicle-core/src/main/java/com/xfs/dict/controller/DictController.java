package com.xfs.dict.controller;

import com.xfs.base.response.JsonResult;
import com.xfs.base.auth.AuthGuard;
import com.xfs.dict.pojo.dto.DictQuery;
import com.xfs.dict.pojo.dto.DictSaveParam;
import com.xfs.dict.pojo.vo.DictVO;
import com.xfs.dict.service.DictService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典模块")
@Slf4j
@RestController
@RequestMapping("/v1/dict")
public class DictController {
    @Autowired
    DictService dictService;
    @Autowired
    AuthGuard authGuard;


    @Operation(summary = "查询字典列表")
    @ApiOperationSupport(order = 10)
    @GetMapping("/select")
    public JsonResult selectDict(DictQuery dictQuery){
        log.debug("查询字典:dictQuery={}",dictQuery);
        authGuard.requireLoginUser();
        List<DictVO> list = dictService.selectDict(dictQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "保存字典")
    @ApiOperationSupport(order = 20)
    @PostMapping("/save")
    public JsonResult saveDict(@Validated DictSaveParam dictSaveParam){
        log.debug("保存字典:dictSaveParam={}",dictSaveParam);
        authGuard.requireDispatcher();
        dictService.saveDict(dictSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除字典")
    @ApiOperationSupport(order = 30)
    @PostMapping("/delete/{id}")
    public JsonResult deleteDict(@PathVariable Long id){
        log.debug("删除字典:id={}",id);
        authGuard.requireDispatcher();
        dictService.deleteDict(id);
        return JsonResult.ok();
    }
}
