package com.xfs.org.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.xfs.base.auth.AuthGuard;
import com.xfs.base.response.JsonResult;
import com.xfs.org.pojo.dto.OrgQuery;
import com.xfs.org.pojo.dto.OrgSaveParam;
import com.xfs.org.service.OrgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "组织管理模块")
@RestController
@RequestMapping("/v1/org")
@Slf4j
public class OrgController {
    @Autowired
    private OrgService orgService;
    @Autowired
    private AuthGuard authGuard;

    @Operation(summary = "查询组织")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult selectOrg(OrgQuery orgQuery) {
        authGuard.requireMenuPerm("org:manage");
        return JsonResult.ok(orgService.selectOrg(orgQuery));
    }

    @Operation(summary = "保存组织")
    @ApiOperationSupport(order = 20)
    @PostMapping("save")
    public JsonResult saveOrg(@Validated OrgSaveParam orgSaveParam) {
        authGuard.requireMenuPerm("org:manage");
        orgService.saveOrg(orgSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除组织")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult deleteOrg(@PathVariable Long id) {
        authGuard.requireMenuPerm("org:manage");
        orgService.deleteOrg(id);
        return JsonResult.ok();
    }
}
