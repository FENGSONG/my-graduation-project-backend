package com.xfs.role.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.xfs.base.auth.AuthGuard;
import com.xfs.base.response.JsonResult;
import com.xfs.role.pojo.dto.RoleQuery;
import com.xfs.role.pojo.dto.RoleSaveParam;
import com.xfs.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "角色权限模块")
@RestController
@RequestMapping("/v1/role")
@Slf4j
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private AuthGuard authGuard;

    @Operation(summary = "查询角色")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult selectRole(RoleQuery roleQuery) {
        authGuard.requireMenuPerm("role:manage");
        return JsonResult.ok(roleService.selectRole(roleQuery));
    }

    @Operation(summary = "保存角色")
    @ApiOperationSupport(order = 20)
    @PostMapping("save")
    public JsonResult saveRole(@Validated RoleSaveParam roleSaveParam) {
        authGuard.requireMenuPerm("role:manage");
        roleService.saveRole(roleSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除角色")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult deleteRole(@PathVariable Long id) {
        authGuard.requireMenuPerm("role:manage");
        roleService.deleteRole(id);
        return JsonResult.ok();
    }
}
