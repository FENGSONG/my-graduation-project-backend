package com.xfs.role.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RoleQuery {
    @Schema(description = "角色ID")
    private Long id;
    @Schema(description = "角色编码")
    private String roleCode;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "状态")
    private String status;
}
