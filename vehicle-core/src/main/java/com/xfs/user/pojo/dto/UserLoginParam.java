package com.xfs.user.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginParam {
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")//null "" " "都不行!
    private String username;
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 10, message = "密码长度必须在6-10个字符之间")
    private String password;
}
