package com.xfs.dictoption.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DictOptionQuery {
    @Schema(description = "字典项编号")
    private Long id;
    @Schema(description = "字典编号")
    private Long dictId;
    @Schema(description = "字典项名称")
    private String label;
}
