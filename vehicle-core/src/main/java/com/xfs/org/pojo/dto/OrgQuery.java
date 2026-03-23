package com.xfs.org.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrgQuery {
    @Schema(description = "组织ID")
    private Long id;
    @Schema(description = "组织名称")
    private String orgName;
    @Schema(description = "组织类型（HQ/COMPANY/DEPT）")
    private String orgType;
    @Schema(description = "父级组织ID")
    private Long parentId;
    @Schema(description = "组织状态")
    private String status;
    @Schema(description = "企业ID")
    private Long enterpriseId;
}
