package com.xfs.dictoption.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DictOptionSaveParam {
    @Schema(description = "字典项id")
    private Long id;
    @Schema(description = "字典id")
    private Long dictId;
    @Schema(description = "字典项名称")
    @NotBlank(message = "字典项名称不能为空")
    private String label;
    @Schema(description = "字典项值")
    @NotBlank(message = "字典项值不能为空")
    private String value;
    @Schema(description = "字典项排序")
    @NotNull(message = "字典项排序不能为空")
    private Integer sort;
    @Schema(description = "备注信息")
    @NotBlank(message = "备注信息不能为空")
    private String remark;
}
