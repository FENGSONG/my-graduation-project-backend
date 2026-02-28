package com.xfs.application.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ApplicationVO {
    private Long id;//申请单id
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
    private String status;//申请单状态
    private Long vehicleId;//分配车辆id
    private String rejectReason;//驳回原因
    private List<Long> auditUserIdList;//审批人id集合 [106,103]
    private String auditUsernameList;//审批人姓名字符串 "
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;//创建时间
}
