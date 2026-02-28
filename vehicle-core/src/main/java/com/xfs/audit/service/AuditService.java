package com.xfs.audit.service;

import com.xfs.application.pojo.entity.Application;
import com.xfs.audit.pojo.dto.AuditQuery;
import com.xfs.audit.pojo.dto.AuditSaveParam;
import com.xfs.audit.pojo.vo.AuditVO;

import java.util.List;

public interface AuditService {
    void insertAudit(Application application);

    List<AuditVO> selectAuditByApplicationId(Long id);

    List<AuditVO> selectAudit(AuditQuery auditQuery);

    void updateAudit(AuditSaveParam auditSaveParam);
}
