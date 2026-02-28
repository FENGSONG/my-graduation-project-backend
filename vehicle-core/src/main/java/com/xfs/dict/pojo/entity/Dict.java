package com.xfs.dict.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Dict {
    private Long id;// 字典编号
    private String name;// 字典名称
    private String code;// 字典编码
    private String remark;// 字典备注
    private Date createTime;// 创建时间
    private Date updateTime;// 更新时间
    private String status;// 字典状态 1=正常,0=禁用
}
