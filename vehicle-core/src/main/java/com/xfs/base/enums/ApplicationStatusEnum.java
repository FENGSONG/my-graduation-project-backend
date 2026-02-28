package com.xfs.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationStatusEnum {
    PENDING("10","已发起"),
    CANCEL("20","撤销"),
    AUDIT("30","审核中"),
    REJECT("40","驳回"),
    AUDITED("50","已审核"),
    ALLOCATION("60","已分配用车"),
    END("70","工单结束");

    private String code;
    private String msg;
}
