package com.xfs.audit.mapper;

import com.xfs.audit.pojo.dto.AuditQuery;
import com.xfs.audit.pojo.entity.Audit;
import com.xfs.audit.pojo.vo.AuditVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AuditMapper {
    void insert(Audit audit);

    List<AuditVO> selectAuditByApplicationId(Long id);

    void deleteByApplicationId(Long id);

    List<AuditVO> selectAudit(AuditQuery auditQuery);

    void update(Audit audit);

    int updateIfUserAndStatus(@Param("id") Long id,
                              @Param("auditUserId") Long auditUserId,
                              @Param("expectStatus") String expectStatus,
                              @Param("targetStatus") String targetStatus,
                              @Param("updateTime") Date updateTime);

    Integer selectRestAuditCount(AuditQuery auditQuery);

    void deleteById(Long id);
}
