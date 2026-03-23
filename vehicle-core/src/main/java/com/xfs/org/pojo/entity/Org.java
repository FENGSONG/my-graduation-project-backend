package com.xfs.org.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Org {
    private Long id;
    private String orgName;
    private String orgType;
    private Long parentId;
    private Integer orgLevel;
    private Long enterpriseId;
    private Long companyId;
    private Long leaderUserId;
    private Integer sort;
    private String status;
    private Date createTime;
    private Date updateTime;
}
