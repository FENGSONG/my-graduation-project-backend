package com.xfs.audit.mapper;

import com.xfs.audit.pojo.dto.AuditQuery;
import com.xfs.audit.pojo.dto.AuditSaveParam;
import com.xfs.audit.pojo.entity.Audit;
import com.xfs.audit.pojo.vo.AuditVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditMapper {
    void insert(Audit audit);

    List<AuditVO> selectAuditByApplicationId(Long id);

    void deleteByApplicationId(Long id);

    List<AuditVO> selectAudit(AuditQuery auditQuery);

    void update(Audit audit);

    Integer selectRestAuditCount(AuditQuery auditQuery);

    void deleteById(Long id);
}
