package com.xfs.user.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String password;
    private String token;
    private String email;
    private String phone;
    private Integer age;
    private String gender;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private String status;
    private String level;
    private Long parentId;
}
