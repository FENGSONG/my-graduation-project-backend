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
import com.xfs.user.mapper.UserMapper;
import com.xfs.user.pojo.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
        Audit audit = new Audit();
        BeanUtils.copyProperties(auditSaveParam, audit);
        audit.setUpdateTime(new Date());
        // 1. 先将当前操作人(例如 moly)的记录更新为已通过(30)
        auditMapper.update(audit);

        Application application = new Application();
        application.setId(audit.getApplicationId());
        application.setUpdateTime(new Date());

        if(audit.getAuditStatus().equals(AuditStatusEnum.AUDITED.getCode())){ // 30 审核通过处理

            // ================== 核心修复：精准寻找下一位审批人 ==================
            AuditQuery query = new AuditQuery();
            query.setApplicationId(audit.getApplicationId());
            // 把该单据所有的审批流（moly, tom, shaoyun）一口气全查出来
            List<AuditVO> allAudits = auditMapper.selectAudit(query);

            AuditVO nextAudit = null;
            // 遍历寻找状态仍然是 "20" (待他人审核) 并且排序最靠前的那个人
            for (AuditVO vo : allAudits) {
                if (AuditStatusEnum.PENDING.getCode().equals(vo.getAuditStatus())) {
                    if (nextAudit == null || vo.getAuditSort() < nextAudit.getAuditSort()) {
                        nextAudit = vo; // 精准锁定 tom
                    }
                }
            }

            if (nextAudit != null) {
                // 找到了下一个人（tom），把他的状态改成 "10" (待我审核)，从而在他的列表里显现
                log.info("流转成功！即将激活下一位审批人: 用户ID={}, 排序={}", nextAudit.getAuditUserId(), nextAudit.getAuditSort());
                Audit audit2 = new Audit();
                audit2.setId(nextAudit.getId());
                audit2.setAuditStatus(AuditStatusEnum.MY_PENDING.getCode()); // 10
                audit2.setUpdateTime(new Date());
                auditMapper.update(audit2);

                // 申请单仍保持审核中
                application.setStatus(ApplicationStatusEnum.AUDIT.getCode());
                applicationMapper.update(application);
            } else {
                // 如果没有状态为 20 的人了，说明最后一位大领导(shaoyun)也批完了！
                log.info("所有领导审批完毕！申请单 {} 正式通过", audit.getApplicationId());
                application.setStatus(ApplicationStatusEnum.AUDITED.getCode());
                applicationMapper.update(application);

                // 触发分车调度
                applicationService.autoDistribute(audit.getApplicationId());
            }
            // =================================================================

        } else if(audit.getAuditStatus().equals(AuditStatusEnum.REJECT.getCode())){ // 40 驳回处理
            AuditQuery auditQuery = new AuditQuery();
            auditQuery.setApplicationId(audit.getApplicationId());
            List<AuditVO> auditVOList = auditMapper.selectAudit(auditQuery);
            if(auditVOList != null && auditVOList.size() > 0){
                for(int i = 0;i < auditVOList.size();i++){
                    AuditVO auditVO = auditVOList.get(i);
                    // 删掉排在后面还没轮到审批的领导记录
                    if(auditVO.getAuditStatus().equals(AuditStatusEnum.PENDING.getCode())){
                        auditMapper.deleteById(auditVO.getId());
                    }
                }
            }
            application.setStatus(ApplicationStatusEnum.REJECT.getCode());
            application.setRejectReason(auditSaveParam.getRejectReason());
            applicationMapper.update(application);
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