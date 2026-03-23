package com.xfs.vehicleinspection.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VehicleInspectionSaveParam {
    @Schema(description = "年检记录ID")
    private Long id;
    @Schema(description = "证书编号")
    @NotBlank(message = "年检证书编号不能为空")
    private String certNo;
    @Schema(description = "有效期")
    @NotNull(message = "有效期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date validUntil;
    @Schema(description = "年检年度")
    @NotBlank(message = "年检年度不能为空")
    private String inspectYear;
    @Schema(description = "年检时间")
    @NotNull(message = "年检时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date inspectTime;
    @Schema(description = "年检项目")
    @NotBlank(message = "年检项目不能为空")
    private String inspectItem;
    @Schema(description = "年检费用")
    @NotNull(message = "年检费用不能为空")
    private BigDecimal fee;
    @Schema(description = "年检地点")
    @NotBlank(message = "年检地点不能为空")
    private String location;
    @Schema(description = "车辆ID")
    @NotNull(message = "年检车辆不能为空")
    private Long vehicleId;
    @Schema(description = "年检人员")
    @NotBlank(message = "年检人员不能为空")
    private String inspector;
    @Schema(description = "送检人员")
    private String sender;
    @Schema(description = "年检状态")
    @NotBlank(message = "年检状态不能为空")
    private String status;
    @Schema(description = "备注")
    private String remark;
}
