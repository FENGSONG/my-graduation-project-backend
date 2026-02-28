package com.xfs.user.controller;

import com.xfs.base.response.JsonResult;
import com.xfs.user.pojo.dto.UserLoginParam;
import com.xfs.user.pojo.dto.UserQuery;
import com.xfs.user.pojo.dto.UserSaveParam;
import com.xfs.user.pojo.vo.UserVO;
import com.xfs.user.service.UserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户模块")
@Slf4j
@RestController
@RequestMapping("/v1/user")
public class UserController {
    @Autowired
    UserService userService;//多态

    @Operation(summary = "用户登录")
    @ApiOperationSupport(order = 10)
    @PostMapping("login")
    public JsonResult login(@Validated @RequestBody UserLoginParam userLoginParam) {
        log.debug("用户登录:userLoginParam={}",userLoginParam);
        //鼠标点击方法名,配合快捷键: Ctrl去接口 Ctrl+Alt去实现类
        UserVO userVO = userService.login(userLoginParam);
        return JsonResult.ok(userVO);
    }

    @Operation(summary = "查询用户")
    @ApiOperationSupport(order = 20)
    @GetMapping("select")
    public JsonResult selectUser(UserQuery userQuery){
        log.debug("查询用户:userQuery={}",userQuery);
        List<UserVO> list = userService.selectUser(userQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "保存用户")
    @ApiOperationSupport(order = 30)
    @PostMapping("save")
    public JsonResult saveUser(@Validated UserSaveParam userSaveParam){
        log.debug("保存用户:userSaveParam={}",userSaveParam);
        userService.saveUser(userSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "重置密码")
    @ApiOperationSupport(order = 40)
    @PostMapping("update/password/{userId}")
    public JsonResult resetPassword(@PathVariable Long userId){
        log.debug("重置密码:userId={}",userId);
        userService.resetPassword(userId);
        return JsonResult.ok();
    }

    @Operation(summary = "修改状态")
    @ApiOperationSupport(order = 50)
    @PostMapping("update/status/{userId}/{status}")
    public JsonResult updateStatus(
            @PathVariable Long userId,@PathVariable String status){
        log.debug("修改状态:userId={},status={}",userId,status);
        userService.updateStatus(userId,status);
        return JsonResult.ok();
    }

    @Operation(summary = "删除用户")
    @ApiOperationSupport(order = 60)
    @PostMapping("delete/{userId}")
    public JsonResult deleteUser(@PathVariable Long userId){
        log.debug("删除用户:userId={}",userId);
        userService.deleteUser(userId);
        return JsonResult.ok();
    }

    @Operation(summary = "查询审批人列表")
    @ApiOperationSupport(order = 70)
    @GetMapping("select/audit/{parentId}")
    public JsonResult selectAuditList(@PathVariable Long parentId){
        log.debug("查询审批人列表:parentId={}",parentId);
        List<UserVO> list = userService.selectAuditUserList(parentId);
        return JsonResult.ok(list);
    }


}
