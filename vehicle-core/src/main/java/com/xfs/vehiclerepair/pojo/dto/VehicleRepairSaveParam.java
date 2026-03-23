package com.xfs.vehiclerepair.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VehicleRepairSaveParam {
    @Schema(description = "维修记录ID")
    private Long id;
    @Schema(description = "维修项目")
    @NotBlank(message = "维修项目不能为空")
    private String repairItem;
    @Schema(description = "车辆ID")
    @NotNull(message = "维修车辆不能为空")
    private Long vehicleId;
    @Schema(description = "维修人员")
    @NotBlank(message = "维修人员不能为空")
    private String repairPerson;
    @Schema(description = "维修地点")
    @NotBlank(message = "维修地点不能为空")
    private String location;
    @Schema(description = "维修时间")
    @NotNull(message = "维修时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date repairTime;
    @Schema(description = "维修费用")
    @NotNull(message = "维修费用不能为空")
    private BigDecimal fee;
    @Schema(description = "维修情况")
    private String conditionDesc;
    @Schema(description = "维修材料")
    private String materialDesc;
    @Schema(description = "送修人员")
    private String sender;
    @Schema(description = "备注")
    private String remark;
}
