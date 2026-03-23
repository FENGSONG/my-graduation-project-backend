package com.xfs.user.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserQuery {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "账号（用户名/邮箱/手机号）")
    private String account;
    @Schema(description = "用户状态")
    private String status;
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "用户职级")
    private String level;
    @Schema(description = "角色编码")
    private String roleCode;
    @Schema(description = "组织ID")
    private Long orgId;

    @Schema(description = "当前登录用户ID（服务端填充）")
    private Long currentUserId;
    @Schema(description = "数据权限范围（ALL/ENTERPRISE/COMPANY/DEPT/SELF）")
    private String dataScope;
    @Schema(description = "当前登录用户企业ID（服务端填充）")
    private Long currentEnterpriseId;
    @Schema(description = "当前登录用户公司ID（服务端填充）")
    private Long currentCompanyId;
    @Schema(description = "当前登录用户部门ID（服务端填充）")
    private Long currentDeptId;
}
