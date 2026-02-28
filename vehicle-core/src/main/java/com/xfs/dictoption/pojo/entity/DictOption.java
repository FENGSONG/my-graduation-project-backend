package com.xfs.dictoption.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DictOption {
    private Long id;//字典项id
    private Long dictId;//字典id
    private String label;//字典项名称
    private String value;//字典项值
    private Integer sort;//字典项排序
    private String remark;//备注信息
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
}
