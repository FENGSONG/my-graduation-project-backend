package com.xfs.application.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class ApplicationSaveParam {
    @Schema(description = "申请单id")
    private Long id;
    @Schema(description = "申请人id")
    private Long userId;
    @Schema(description = "申请人姓名")
    @NotBlank(message = "申请人姓名不能为空")
    private String username;
    @Schema(description = "用车开始时间")
    @NotNull(message = "用车开始时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @Schema(description = "用车结束时间")
    @NotNull(message = "用车结束时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @Schema(description = "驾照图片")
    @NotBlank(message = "驾照图片不能为空")
    private String imgUrl;
    @Schema(description = "出发地")
    @NotBlank(message = "出发地不能为空")
    private String departureAddr;
    @Schema(description = "目的地")
    @NotBlank(message = "目的地不能为空")
    private String destinationAddr;
    @Schema(description = "用车原因")
    @NotBlank(message = "用车原因不能为空")
    private String reason;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "申请单状态")
    private String status;
    @Schema(description = "分配车辆id")
    private Long vehicleId;
    @Schema(description = "驳回原因")
    private String rejectReason;
    @Schema(description = "审批人id集合")
    private List<Long> auditUserIdList;
    //新增时间 更新时间 审批人姓名字符串不需要前端传过来 可以删掉
}
