package com.xfs.role.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleSaveParam {
    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色编码")
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @Schema(description = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "功能权限编码，逗号分隔")
    private String menuPerms;

    @Schema(description = "数据权限（ALL/ENTERPRISE/COMPANY/DEPT/SELF）")
    @NotBlank(message = "数据权限不能为空")
    private String dataScope;

    @Schema(description = "状态")
    @NotBlank(message = "状态不能为空")
    private String status;

    @Schema(description = "备注")
    private String remark;
}
