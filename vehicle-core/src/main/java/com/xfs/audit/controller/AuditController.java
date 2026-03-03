package com.xfs.audit.controller;

import com.xfs.audit.pojo.dto.AuditQuery;
import com.xfs.audit.pojo.dto.AuditSaveParam;
import com.xfs.audit.pojo.vo.AuditVO;
import com.xfs.audit.service.AuditService;
import com.xfs.base.response.JsonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // 🍎 新增：引入 RequestBody 注解
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "审批模块")
@Slf4j
@RestController
@RequestMapping("/v1/audit")
public class AuditController {
    @Autowired
    AuditService auditService;

    @Operation(summary = "查询审批单")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult selectAudit(AuditQuery auditQuery) {
        log.debug("查询审批单:auditQuery={}",auditQuery);
        List<AuditVO> list = auditService.selectAudit(auditQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "审批申请单")
    @ApiOperationSupport(order = 20)
    @PostMapping("update")
    // 🍎 核心修改：加上了 @RequestBody，让 Spring Boot 能够正确解析前端发来的 JSON 数据
    public JsonResult updateAudit(@RequestBody @Validated AuditSaveParam auditSaveParam){
        log.debug("审批申请单:{}",auditSaveParam);
        auditService.updateAudit(auditSaveParam);
        return JsonResult.ok();
    }
}