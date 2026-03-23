package com.xfs.user.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginParam {
    @Schema(description = "登录账号（支持用户名/邮箱/手机号）")
    private String account;

    @Schema(description = "兼容旧版字段（用户名）")
    private String username;

    @Schema(description = "登录类型（PASSWORD/PHONE）")
    private String loginType;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 10, message = "密码长度必须在6-10个字符之间")
    private String password;
}
