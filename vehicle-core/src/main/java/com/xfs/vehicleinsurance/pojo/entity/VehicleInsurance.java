package com.xfs.vehicleinsurance.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VehicleInsurance {
    private Long id;
    private String policyNo;
    private String company;
    private BigDecimal fee;
    private Date insuredTime;
    private Date startTime;
    private Date endTime;
    private String applicant;
    private String insuredPerson;
    private String contact;
    private Long vehicleId;
    private String insuranceType;
    private String status;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
