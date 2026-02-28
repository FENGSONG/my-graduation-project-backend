package com.xfs.dict.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DictVO {
    private Long id;// 字典编号
    private String name;// 字典名称
    private String code;// 字典编码
    private String remark;// 字典备注
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;// 创建时间
}
