package com.xfs.base.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StatusCode {
    OPERATION_SUCCESS(2000, "操作成功"),
    OPERATION_FAILED(3000, "操作失败"),
    VALIDATE_ERROR(3001, "参数校验失败"),
    DATA_UNEXISTS(3002, "请求数据不存在"),
    USERNAME_ALREADY_EXISTS(3003, "用户名被占用"),
    PASSWORD_ERROR(3004, "用户名或密码错误"),
    USERNAME_ERROR(3005, "用户名或密码错误"),
    LICENSE_EXISTS(3006, "车牌号已存在"),
    VEHICLE_EXISTS(3007, "该围栏上存在未移除车辆");

    //状态码、状态码描述
    private Integer code;
    private String message;
}
