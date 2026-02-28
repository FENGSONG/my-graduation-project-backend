package com.xfs.geofence.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GeofenceQuery {
    @Schema(description = "电子围栏编号")
    private Long id;
    @Schema(description = "电子围栏名称")
    private String name;
    @Schema(description = "电子围栏位置")
    private String status;
}
