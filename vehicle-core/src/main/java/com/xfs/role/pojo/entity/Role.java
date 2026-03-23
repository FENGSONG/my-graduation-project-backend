package com.xfs.role.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Role {
    private Long id;
    private String roleCode;
    private String roleName;
    private String menuPerms;
    private String dataScope;
    private String status;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
