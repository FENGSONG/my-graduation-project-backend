package com.xfs.vehicleinspection.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VehicleInspectionQuery {
    @Schema(description = "年检记录ID")
    private Long id;
    @Schema(description = "车辆ID")
    private Long vehicleId;
    @Schema(description = "证书编号")
    private String certNo;
    @Schema(description = "年检状态")
    private String status;
}
