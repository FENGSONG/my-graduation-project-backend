package com.xfs.role.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RoleVO {
    private Long id;
    private String roleCode;
    private String roleName;
    private String menuPerms;
    private List<String> menuPermList;
    private String dataScope;
    private String status;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
