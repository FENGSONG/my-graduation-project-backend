package com.xfs.user.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class UserSaveParam {
    @Schema(description = "用户id")
    private Long id;
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;
    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号格式不正确")
    private String phone;
    @Schema(description = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    @Schema(description = "年龄")
    @NotNull(message = "年龄不能为空")
    @Range(min = 0,max = 200,message = "年龄范围0-200")
    private Integer age;
    @Schema(description = "职级")
    @NotBlank(message = "职级不能为空")
    private String level;
    @Schema(description = "直属领导id")
    @NotNull(message = "直属领导id不能为空")
    private Long parentId;
    @Schema(description = "组织ID（总部/子公司/部门）")
    private Long orgId;
    @Schema(description = "角色编码")
    private String roleCode;
    @Schema(description = "性别")
    @NotBlank(message = "性别不能为空")
    private String gender;
    @Schema(description = "状态")
    @NotBlank(message = "状态不能为空")
    private String status;
}
