package com.xfs.org.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrgSaveParam {
    @Schema(description = "组织ID")
    private Long id;

    @Schema(description = "组织名称")
    @NotBlank(message = "组织名称不能为空")
    private String orgName;

    @Schema(description = "组织类型（HQ/COMPANY/DEPT）")
    @NotBlank(message = "组织类型不能为空")
    private String orgType;

    @Schema(description = "父级组织ID，一级组织传0")
    @NotNull(message = "父级组织ID不能为空")
    private Long parentId;

    @Schema(description = "组织负责人用户ID")
    private Long leaderUserId;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "状态")
    @NotBlank(message = "状态不能为空")
    private String status;
}
