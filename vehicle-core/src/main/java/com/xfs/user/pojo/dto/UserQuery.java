package com.xfs.user.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserQuery {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "用户状态")
    private String status;
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "用户职级")
    private String level;
}
