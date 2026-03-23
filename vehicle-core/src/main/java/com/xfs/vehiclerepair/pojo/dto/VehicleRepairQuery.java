package com.xfs.vehiclerepair.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VehicleRepairQuery {
    @Schema(description = "维修记录ID")
    private Long id;
    @Schema(description = "车辆ID")
    private Long vehicleId;
    @Schema(description = "维修项目")
    private String repairItem;
    @Schema(description = "维修人员")
    private String repairPerson;
}
