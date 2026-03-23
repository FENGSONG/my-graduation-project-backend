package com.xfs.vehicleinsurance.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VehicleInsuranceSaveParam {
    @Schema(description = "保险记录ID")
    private Long id;
    @Schema(description = "保险单号")
    @NotBlank(message = "保险单号不能为空")
    private String policyNo;
    @Schema(description = "保险公司")
    @NotBlank(message = "保险公司不能为空")
    private String company;
    @Schema(description = "保险费用")
    @NotNull(message = "保险费用不能为空")
    private BigDecimal fee;
    @Schema(description = "投保时间")
    @NotNull(message = "投保时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date insuredTime;
    @Schema(description = "保险开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @Schema(description = "保险截止时间")
    @NotNull(message = "保险截止时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @Schema(description = "投保人")
    @NotBlank(message = "投保人不能为空")
    private String applicant;
    @Schema(description = "被保险人")
    @NotBlank(message = "被保险人不能为空")
    private String insuredPerson;
    @Schema(description = "保险联系人")
    private String contact;
    @Schema(description = "车辆ID")
    @NotNull(message = "保险车辆不能为空")
    private Long vehicleId;
    @Schema(description = "保险类型")
    @NotBlank(message = "保险类型不能为空")
    private String insuranceType;
    @Schema(description = "保险状态")
    private String status;
    @Schema(description = "备注")
    private String remark;
}
