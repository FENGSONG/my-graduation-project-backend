package com.xfs.audit.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Audit {
    private Long id;//审批单id
    private Long applicationId;//申请单id
    private Long auditUserId;//审批人id
    private String auditStatus;//审批状态
    private Integer auditSort;//审批排序
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
}
