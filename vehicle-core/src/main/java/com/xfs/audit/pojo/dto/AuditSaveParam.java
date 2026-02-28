package com.xfs.audit.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuditSaveParam {
    @Schema(description = "审批单id")
    private Long id;
    @Schema(description = "申请单id")
    private Long applicationId;
    @Schema(description = "审批人id")
    private Long auditUserId;
    @Schema(description = "审批状态")
    @NotBlank(message = "审批状态不能为空")
    private String auditStatus;
    @Schema(description = "审批顺序")
    private Integer auditSort;
    @Schema(description = "驳回原因")
    private String rejectReason;
}
