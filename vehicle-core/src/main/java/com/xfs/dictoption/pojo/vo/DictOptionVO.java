package com.xfs.dictoption.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DictOptionVO {
    private Long id;//字典项id
    private Long dictId;//字典id
    private String label;//字典项名称
    private String value;//字典项值
    private Integer sort;//字典项排序
    private String remark;//备注信息
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;//创建时间
}
