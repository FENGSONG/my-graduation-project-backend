package com.xfs.vehicleinspection.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VehicleInspection {
    private Long id;
    private String certNo;
    private Date validUntil;
    private String inspectYear;
    private Date inspectTime;
    private String inspectItem;
    private BigDecimal fee;
    private String location;
    private Long vehicleId;
    private String inspector;
    private String sender;
    private String status;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
