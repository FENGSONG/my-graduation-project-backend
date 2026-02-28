package com.xfs.vehicle.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class VehicleSaveParam {
    @Schema(description = "车辆id")
    private Long id;
    @Schema(description = "车辆品牌")
    @NotBlank(message = "车辆品牌不能为空!")
    private String brand;
    @Schema(description = "车辆车牌")
    @NotBlank(message = "车牌号不能为空!")
    private String license;
    @Schema(description = "车辆型号")
    @NotBlank(message = "车辆型号不能为空!")
    private String model;
    @Schema(description = "车辆识别码")
    @NotBlank(message = "车辆识别码不能为空!")
    private String code;
    @Schema(description = "车辆排量")
    @NotBlank(message = "车辆排量不能为空!")
    private String displacement;
    @Schema(description = "车辆类型")
    @NotBlank(message = "车辆类型不能为空!")
    private String type;
    @Schema(description = "车辆颜色")
    @NotBlank(message = "车辆颜色不能为空!")
    private String color;
    @Schema(description = "车辆里程数")
    @NotBlank(message = "车辆里程数不能为空!")
    private String kilometers;
    @Schema(description = "车辆购买时间")
    @NotNull(message = "车辆购买时间不能为空!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date buyTime;
    @NotNull(message = "车辆上牌时间不能为空!")
    @Schema(description = "车辆上牌时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date regTime;
    @Schema(description = "车辆购买价格")
    @NotBlank(message = "车辆购买价格不能为空!")
    private String price;
    @Schema(description = "车辆电池类型")
    @NotBlank(message = "车辆电池类型不能为空!")
    private String batteryType;
    @Schema(description = "车辆电子围栏绑定状态")
    private String geofenceBindStatus;
    @Schema(description = "车辆电子围栏id")
    private Long geofenceId;
}
