package com.xfs.org.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class OrgVO {
    private Long id;
    private String orgName;
    private String orgType;
    private Long parentId;
    private Integer orgLevel;
    private Long enterpriseId;
    private Long companyId;
    private Long leaderUserId;
    private String leaderUsername;
    private Integer sort;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
