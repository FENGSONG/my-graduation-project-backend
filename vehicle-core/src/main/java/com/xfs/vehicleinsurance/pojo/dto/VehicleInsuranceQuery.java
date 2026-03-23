package com.xfs.vehicleinsurance.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VehicleInsuranceQuery {
    @Schema(description = "保险记录ID")
    private Long id;
    @Schema(description = "车辆ID")
    private Long vehicleId;
    @Schema(description = "保险单号")
    private String policyNo;
    @Schema(description = "保险公司")
    private String company;
    @Schema(description = "保险状态")
    private String status;
}
