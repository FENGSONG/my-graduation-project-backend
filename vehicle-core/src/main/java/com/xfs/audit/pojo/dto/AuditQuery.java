package com.xfs.audit.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuditQuery {
    @Schema(description = "审批单id")
    private Long id;
    @Schema(description = "申请单id")
    private Long applicationId;
    @Schema(description = "审批人id")
    private Long auditUserId;
    @Schema(description = "审批状态")
    private String auditStatus;
    @Schema(description = "审批次序")
    private Integer auditSort;
    @Schema(description = "用车人姓名")
    private String username;
    @Schema(description = "关键字")
    private String keyword;
}
