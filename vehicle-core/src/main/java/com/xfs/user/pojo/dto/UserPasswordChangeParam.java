package com.xfs.user.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserPasswordChangeParam {
    @Schema(description = "旧密码")
    @NotBlank(message = "旧密码不能为空")
    @Size(min = 6, max = 10, message = "旧密码长度必须在6-10个字符之间")
    private String oldPassword;

    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 10, message = "新密码长度必须在6-10个字符之间")
    private String newPassword;
}
