package com.xfs.vehicle.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Vehicle {
    private Long id; // 车辆id
    private String brand; // 车辆品牌
    private String license; // 车牌号
    private String model; // 车辆型号
    private String code; // 车辆识别码
    private String displacement; // 车辆排量
    private String status; // 车辆状态：1空闲 2占用
    private String type; // 车辆类型
    private String color; // 车辆颜色
    private String kilometers; // 车辆里程数
    private Date regTime; // 车辆上牌时间
    private Date buyTime; // 车辆购买时间
    private String price; // 车辆购买价格
    private String batteryType; // 车辆电池类型
    private Date createTime; // 车辆创建时间
    private Date updateTime; // 车辆更新时间
    private String geofenceBindStatus; // 电子围栏绑定状态：0未绑定围栏 1已绑定围栏
    private Long geofenceId; // 电子围栏id
}
