package com.xfs.user.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer age;
    private String gender;
    private Date createTime;
    private Date updateTime;
    private String status;
    private String level;
    private Long parentId;
}
