package com.xfs.dictoption.controller;

import com.xfs.base.response.JsonResult;
import com.xfs.base.auth.AuthGuard;
import com.xfs.dictoption.pojo.dto.DictOptionQuery;
import com.xfs.dictoption.pojo.dto.DictOptionSaveParam;
import com.xfs.dictoption.pojo.vo.DictOptionVO;
import com.xfs.dictoption.service.DictOptionService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典项模块")
@Slf4j
@RestController
@RequestMapping("/v1/dictoption")
public class DictOptionController {
    @Autowired
    DictOptionService dictOptionService;
    @Autowired
    AuthGuard authGuard;

    @Operation(summary = "查询字典项列表")
    @ApiOperationSupport(order = 10)
    @GetMapping("/select")
    public JsonResult selectDictOption(DictOptionQuery dictOptionQuery){
        log.debug("字典项列表参数:{}",dictOptionQuery);
        authGuard.requireLoginUser();
        List<DictOptionVO> list = dictOptionService.selectDictOption(dictOptionQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "保存字典项")
    @ApiOperationSupport(order = 20)
    @PostMapping("/save")
    public JsonResult saveDictOption(@Validated DictOptionSaveParam dictOptionSaveParam){
        log.debug("字典项保存参数:{}",dictOptionSaveParam);
        authGuard.requireDispatcher();
        dictOptionService.saveDictOption(dictOptionSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除字典项")
    @ApiOperationSupport(order = 30)
    @PostMapping("/delete/{id}")
    public JsonResult deleteDictOption(@PathVariable Long id){
        log.debug("字典项删除参数:{}",id);
        authGuard.requireDispatcher();
        dictOptionService.deleteDictOption(id);
        return JsonResult.ok();
    }

    /* 定义一个根据字典code查询其下对应的所有字典项的方法 */
    @Operation(summary = "根据字典code查询其下所有字典项")
    @ApiOperationSupport(order = 40)
    @GetMapping("select/{code}")
    public JsonResult selectDictOptionByCode(@PathVariable String code){
        log.debug("根据字典code查询其下所有字典项参数:{}",code);
        authGuard.requireLoginUser();
        List<DictOptionVO> list = dictOptionService.selectDictOptionByCode(code);
        return JsonResult.ok(list);
    }

}
