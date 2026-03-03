package com.xfs.audit.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AuditVO {
    private Long id;//审批单id
    private Long applicationId;//申请单id
    private Long auditUserId;//审批人id
    private String auditStatus;//审批状态
    private Integer auditSort;//审批排序

    // 🍎 新增：接收审批单的生成时间（可用于前端展示“发起时间”）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //后面的字段直接复制ApplicationVO的字段,因为需要在调度审核里显示申请单的详细数据
    private Long userId;//申请人id
    private String username;//申请人姓名

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;//用车开始时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;//用车结束时间

    private String imgUrl;//驾照图片
    private String departureAddr;//出发地
    private String destinationAddr;//目的地
    private String reason;//用车原因
    private String remark;//备注

    // 🍎 修改：改为 applicationStatus 与 SQL 别名对应，避免和 auditStatus 混淆
    private String applicationStatus;//申请单状态

    private Long vehicleId;//分配车辆id
    private String rejectReason;//驳回原因
    private List<Long> auditUserIdList;//审批人id集合 [106,103]
    private String auditUsernameList;//审批人姓名字符串
}