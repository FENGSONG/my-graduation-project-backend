package com.xfs.dict.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictSaveParam {
    @Schema(description = "字典编号")
    private Long id;
    @Schema(description = "字典名称")
    @NotBlank(message = "字典名称不能为空")
    private String name;
    @Schema(description = "字典编码")
    @NotBlank(message = "字典编码不能为空")
    private String code;
    @Schema(description = "字典备注")
    @NotBlank(message = "字典备注不能为空")
    private String remark;
}
