package com.xfs.geofence.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GeofenceSaveParam {
    @Schema(description = "电子围栏名称")
    @NotBlank(message = "电子围栏名称不能为空")
    private String name;
    @Schema(description = "电子围栏位置数据")
    @NotBlank(message = "电子围栏位置数据不能为空")
    private String position;
}
