package com.xfs.geofence.pojo.vo;

import com.xfs.vehicle.pojo.vo.VehicleVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GeofenceVO {
    private Long id; // 电子围栏编号
    private String name; // 电子围栏名称
    private String status; // 电子围栏状态
    private String position; // 电子围栏位置数据
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime; // 创建时间
    /* 瞬态属性 */
    private Integer totalNum;
    private Integer availableNum;
    private List<VehicleVO> vehicleList;
}
