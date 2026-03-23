package com.xfs.vehiclerepair.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VehicleRepairVO {
    private Long id;
    private String repairItem;
    private Long vehicleId;
    private String repairPerson;
    private String location;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date repairTime;
    private BigDecimal fee;
    private String conditionDesc;
    private String materialDesc;
    private String sender;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
