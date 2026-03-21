package com.xfs.application.mapper;

import com.xfs.application.pojo.dto.ApplicationQuery;
import com.xfs.application.pojo.entity.Application;
import com.xfs.application.pojo.vo.ApplicationVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ApplicationMapper {
    void insert(Application application);

    List<ApplicationVO> selectApplication(ApplicationQuery applicationQuery);

    void update(Application application);

    void back(Application application);

    Application selectById(Long id);

    int updateIfStatus(@Param("application") Application application, @Param("expectStatus") String expectStatus);

    int backIfStatus(@Param("application") Application application, @Param("expectStatus") String expectStatus);

    int cancelIfPendingAndUser(@Param("id") Long id, @Param("userId") Long userId, @Param("updateTime") Date updateTime);
}
