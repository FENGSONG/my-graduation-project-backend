package com.xfs.geofence.pojo.entity;

import com.xfs.vehicle.pojo.vo.VehicleVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Geofence {
    private Long id; // 电子围栏编号
    private String name; // 电子围栏名称
    private String status; // 电子围栏状态
    private String position; // 电子围栏位置数据
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    /* 瞬态属性 */
    private Integer totalNum;
    private Integer availableNum;
    private List<VehicleVO> vehicleList;
}
