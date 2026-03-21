
package com.xfs.application.pojo.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ApplicationQuery {
    @Schema(description = "申请人id")
    private Long userId;
    @Schema(description = "出发地")
    private String departureAddr;
    @Schema(description = "目的地")
    private String destinationAddr;
    @Schema(description = "申请单状态")
    private String status;
    @Schema(description = "用车人姓名")
    private String username;
}
