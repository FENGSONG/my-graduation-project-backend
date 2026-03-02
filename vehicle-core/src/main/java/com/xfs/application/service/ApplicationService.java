package com.xfs.application.service;


import com.xfs.application.pojo.dto.ApplicationQuery;
import com.xfs.application.pojo.dto.ApplicationSaveParam;
import com.xfs.application.pojo.vo.ApplicationVO;

import java.util.List;

public interface ApplicationService {
    void save(ApplicationSaveParam applicationSaveParam);

    List<ApplicationVO> selectApplication(ApplicationQuery applicationQuery);

    void cancel(Long id);

    void distribute(Long applicationId, Long vehicleId);

    void back(Long applicationId, Long vehicleId);

    /**
     * 系统自动分配车辆（基于时间段排期冲突检测）
     * @param applicationId 申请单编号
     * @return true: 分配成功; false: 分配失败（无可用车辆）
     */
    boolean autoDistribute(Long applicationId);
}
