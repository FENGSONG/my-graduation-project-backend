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
}
