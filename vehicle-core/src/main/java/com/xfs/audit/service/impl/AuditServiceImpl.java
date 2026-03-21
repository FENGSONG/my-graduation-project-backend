package com.xfs.audit.service.impl;

import com.xfs.application.mapper.ApplicationMapper;
import com.xfs.application.pojo.entity.Application;
import com.xfs.application.service.ApplicationService;
import com.xfs.audit.mapper.AuditMapper;
import com.xfs.audit.pojo.dto.AuditQuery;
import com.xfs.audit.pojo.dto.AuditSaveParam;
import com.xfs.audit.pojo.entity.Audit;
import com.xfs.audit.pojo.vo.AuditVO;
import com.xfs.audit.service.AuditService;
import com.xfs.base.enums.ApplicationStatusEnum;
import com.xfs.base.enums.AuditStatusEnum;
import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.base.util.AuthTokenUtil;
import com.xfs.user.mapper.UserMapper;
import com.xfs.user.pojo.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@Transactional
@Service
@Slf4j
public class AuditServiceImpl implements AuditService {
    @Autowired
    AuditMapper auditMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ApplicationMapper applicationMapper;

    @Autowired
    @Lazy
    private ApplicationService applicationService;

    @Override
    public void insertAudit(Application application) {
        log.debug("为当前申请单生成对应审批单的业务，参数：{}", application);
        List<Long> userIdList = application.getAuditUserIdList();
        for(int i = 0; i < userIdList.size(); i++){
            Audit audit = new Audit();
            audit.setApplicationId(application.getId());
            audit.setAuditUserId(userIdList.get(i));
            audit.setAuditSort(i);
            audit.setCreateTime(new Date());
            if(i == 0){
                audit.setAuditStatus(AuditStatusEnum.MY_PENDING.getCode());
            }else{
                audit.setAuditStatus(AuditStatusEnum.PENDING.getCode());
            }
            auditMapper.insert(audit);
        }
    }

    @Override
    public List<AuditVO> selectAuditByApplicationId(Long id) {
        return auditMapper.selectAuditByApplicationId(id);
    }

    @Override
    public List<AuditVO> selectAudit(AuditQuery auditQuery) {
        log.debug("查询审批单的业务:auditQuery={}",auditQuery);
        List<AuditVO> auditVOList = auditMapper.selectAudit(auditQuery);
        for (int i = 0;i<auditVOList.size();i++){
            AuditVO auditVO = auditVOList.get(i);
            assignAuditUserList(auditVO);
        }
        return auditVOList;
    }

    @Override
    public void updateAudit(AuditSaveParam auditSaveParam) {
        log.debug("审批申请单业务:auditSaveParam={}",auditSaveParam);
        Long loginUserId = AuthTokenUtil.getCurrentUserId();
        if (auditSaveParam.getId() == null || auditSaveParam.getId() <= 0) {
            throw new ServiceException(StatusCode.VALIDATE_ERROR);
        }

        String targetStatus = auditSaveParam.getAuditStatus();
        boolean pass = AuditStatusEnum.AUDITED.getCode().equals(targetStatus);
        boolean reject = AuditStatusEnum.REJECT.getCode().equals(targetStatus);
        if (!pass && !reject) {
            throw new ServiceException(StatusCode.VALIDATE_ERROR);
        }

        AuditQuery currentQuery = new AuditQuery();
        currentQuery.setId(auditSaveParam.getId());
        List<AuditVO> currentList = auditMapper.selectAudit(currentQuery);
        if (currentList == null || currentList.isEmpty()) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
        AuditVO currentAudit = currentList.get(0);
        if (auditSaveParam.getApplicationId() != null
                && !auditSaveParam.getApplicationId().equals(currentAudit.getApplicationId())) {
            throw new ServiceException(StatusCode.FORBIDDEN);
        }
        if (!loginUserId.equals(currentAudit.getAuditUserId())) {
            throw new ServiceException(StatusCode.FORBIDDEN);
        }
        if (!AuditStatusEnum.MY_PENDING.getCode().equals(currentAudit.getAuditStatus())) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }

        int currentRows = auditMapper.updateIfUserAndStatus(
                currentAudit.getId(),
                loginUserId,
                AuditStatusEnum.MY_PENDING.getCode(),
                targetStatus,
                new Date()
        );
        if (currentRows != 1) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }

        Long applicationId = currentAudit.getApplicationId();
        Application application = new Application();
        application.setId(applicationId);
        application.setUpdateTime(new Date());

        if (pass) { // 30 审核通过处理
            AuditQuery query = new AuditQuery();
            query.setApplicationId(applicationId);
            List<AuditVO> allAudits = auditMapper.selectAudit(query);

            AuditVO nextAudit = null;
            for (AuditVO vo : allAudits) {
                if (AuditStatusEnum.PENDING.getCode().equals(vo.getAuditStatus())) {
                    if (nextAudit == null || vo.getAuditSort() < nextAudit.getAuditSort()) {
                        nextAudit = vo;
                    }
                }
            }

            if (nextAudit != null) {
                int nextRows = auditMapper.updateIfUserAndStatus(
                        nextAudit.getId(),
                        nextAudit.getAuditUserId(),
                        AuditStatusEnum.PENDING.getCode(),
                        AuditStatusEnum.MY_PENDING.getCode(),
                        new Date()
                );
                if (nextRows == 1) {
                    log.info("流转成功！即将激活下一位审批人: 用户ID={}, 排序={}", nextAudit.getAuditUserId(), nextAudit.getAuditSort());
                }

                application.setStatus(ApplicationStatusEnum.AUDIT.getCode());
                int appRows = applicationMapper.updateIfStatus(application, ApplicationStatusEnum.PENDING.getCode());
                if (appRows == 0) {
                    applicationMapper.updateIfStatus(application, ApplicationStatusEnum.AUDIT.getCode());
                }
            } else {
                log.info("所有领导审批完毕！申请单 {} 正式通过", applicationId);
                application.setStatus(ApplicationStatusEnum.AUDITED.getCode());
                int appRows = applicationMapper.updateIfStatus(application, ApplicationStatusEnum.PENDING.getCode());
                if (appRows == 0) {
                    appRows = applicationMapper.updateIfStatus(application, ApplicationStatusEnum.AUDIT.getCode());
                }
                if (appRows == 0) {
                    throw new ServiceException(StatusCode.ILLEGAL_STATUS);
                }

                // 只有最终通过后才允许分配车辆
                applicationService.autoDistribute(applicationId);
            }
        } else { // 40 驳回处理
            AuditQuery auditQuery = new AuditQuery();
            auditQuery.setApplicationId(applicationId);
            List<AuditVO> auditVOList = auditMapper.selectAudit(auditQuery);
            if(auditVOList != null && !auditVOList.isEmpty()){
                for (AuditVO auditVO : auditVOList) {
                    // 删掉排在后面还没轮到审批的领导记录
                    if(AuditStatusEnum.PENDING.getCode().equals(auditVO.getAuditStatus())){
                        auditMapper.deleteById(auditVO.getId());
                    }
                }
            }
            application.setStatus(ApplicationStatusEnum.REJECT.getCode());
            application.setRejectReason(auditSaveParam.getRejectReason());
            int appRows = applicationMapper.updateIfStatus(application, ApplicationStatusEnum.PENDING.getCode());
            if (appRows == 0) {
                appRows = applicationMapper.updateIfStatus(application, ApplicationStatusEnum.AUDIT.getCode());
            }
            if (appRows == 0) {
                throw new ServiceException(StatusCode.ILLEGAL_STATUS);
            }
        }
    }

    private void assignAuditUserList(AuditVO auditVO) {
        List<String> auditUsernameList = new ArrayList<>();
        List<Long> auditUserIdList = new ArrayList<>();
        List<AuditVO> auditVOList = auditMapper.selectAuditByApplicationId(auditVO.getApplicationId());
        for(int i = 0;i <auditVOList.size();i++){
            Long userId = auditVOList.get(i).getAuditUserId();
            auditUserIdList.add(userId);
            UserVO user = userMapper.selectById(userId);
            if(user != null) {
                auditUsernameList.add(user.getUsername());
            }
        }
        StringJoiner stringJoiner = new StringJoiner(",");
        for (String username : auditUsernameList){
            stringJoiner.add(username);
        }
        auditVO.setAuditUserIdList(auditUserIdList);
        auditVO.setAuditUsernameList(stringJoiner.toString());
    }
}
