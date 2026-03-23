package com.xfs.vehiclerepair.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VehicleRepair {
    private Long id;
    private String repairItem;
    private Long vehicleId;
    private String repairPerson;
    private String location;
    private Date repairTime;
    private BigDecimal fee;
    private String conditionDesc;
    private String materialDesc;
    private String sender;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
