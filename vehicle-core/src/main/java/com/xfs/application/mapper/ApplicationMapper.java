package com.xfs.application.mapper;

import com.xfs.application.pojo.dto.ApplicationQuery;
import com.xfs.application.pojo.entity.Application;
import com.xfs.application.pojo.vo.ApplicationVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationMapper {
    void insert(Application application);

    List<ApplicationVO> selectApplication(ApplicationQuery applicationQuery);

    void update(Application application);

    void back(Application application);
}
