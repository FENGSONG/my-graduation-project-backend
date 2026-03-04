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

        // 🍎 健壮性保护：防空指针
        if (userIdList == null || userIdList.isEmpty()) {
            log.warn("申请单 {} 没有审批人，不生成审批流", application.getId());
            return;
        }

        for(int i = 0; i < userIdList.size(); i++){
            Audit audit = new Audit();
            audit.setApplicationId(application.getId());
            audit.setAuditUserId(userIdList.get(i));
            audit.setAuditSort(i);
            audit.setCreateTime(new Date());

            if(i == 0){
                audit.setAuditStatus("10"); // 第一级：待我审核
            }else{
                audit.setAuditStatus("20"); // 后续级：待他人审核
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
        log.debug("====== 审批流转处理开始，参数：{} ======", auditSaveParam);

        // 1. 更新当前审批单状态（同意或驳回）
        Audit audit = new Audit();
        BeanUtils.copyProperties(auditSaveParam, audit);
        audit.setUpdateTime(new Date());
        auditMapper.update(audit);

        // 2. 准备更新主申请单的对象
        Application application = new Application();
        application.setId(audit.getApplicationId());
        application.setUpdateTime(new Date());

        // ========================================================
        // 🍎 核心逻辑分支：同意(30) or 驳回(40)
        // 使用 String.valueOf() 确保无论前后端传的是字符串还是数字都能匹配
        String currentStatus = String.valueOf(audit.getAuditStatus());

        if("30".equals(currentStatus)) {
            // ----- 【同意处理分支】 -----

            // 查询该申请单下的所有审批节点（通过 selectAuditByApplicationId 方法，这个方法你肯定写对了）
            List<AuditVO> allAudits = auditMapper.selectAuditByApplicationId(audit.getApplicationId());

            // 寻找下一个审批人节点：即 auditSort 比当前大 1 的那条记录
            AuditVO nextAudit = null;
            Integer currentSort = audit.getAuditSort();

            if (allAudits != null && currentSort != null) {
                for (AuditVO a : allAudits) {
                    if (a.getAuditSort() == currentSort + 1) {
                        nextAudit = a;
                        break;
                    }
                }
            }

            if(nextAudit != null) {
                // 场景 A：还有下一个审批人！
                log.info("申请单 {} 已通过第 {} 级审批，正在流转给第 {} 级...", audit.getApplicationId(), currentSort, currentSort + 1);

                // 1. 唤醒下一个审批人（把他的状态从 20 改为 10）
                Audit updateNext = new Audit();
                updateNext.setId(nextAudit.getId());
                updateNext.setAuditStatus("10"); // 10代表“待我审核”
                updateNext.setUpdateTime(new Date());
                auditMapper.update(updateNext);

                // 2. 将主申请单状态设置为“审核中(30)”
                application.setStatus("30");
                applicationMapper.update(application);

            } else {
                // 场景 B：没有下一个审批人了！（终审通过）
                log.info("申请单 {} 所有审批已全票通过！正在触发自动派车...", audit.getApplicationId());

                // 1. 先将申请单状态更新为“已通过(50)”
                application.setStatus("50");
                applicationMapper.update(application);

                // 2. 触发自动分车
                boolean isSuccess = applicationService.autoDistribute(audit.getApplicationId());

                if (isSuccess) {
                    log.info("🎉 申请单 {} 自动分车成功！", audit.getApplicationId());
                } else {
                    log.warn("⚠️ 申请单 {} 指定时间段无空车，流转至人工调度。", audit.getApplicationId());
                }
            }

        } else if("40".equals(currentStatus)) {
            // ----- 【驳回处理分支】 -----
            log.info("申请单 {} 被驳回！终止后续审批流...", audit.getApplicationId());

            // 找出并删除后续那些还没开始审的节点（状态为 20 的）
            List<AuditVO> allAudits = auditMapper.selectAuditByApplicationId(audit.getApplicationId());
            if(allAudits != null) {
                for(AuditVO a : allAudits) {
                    if("20".equals(String.valueOf(a.getAuditStatus()))) {
                        auditMapper.deleteById(a.getId());
                    }
                }
            }

            // 将主申请单状态直接改为“驳回(40)”
            application.setStatus("40");
            application.setRejectReason(auditSaveParam.getRejectReason());
            applicationMapper.update(application);
        }
        log.debug("====== 审批流转处理结束 ======");
    }

    private void assignAuditUserList(AuditVO auditVO) {
        List<String> auditUsernameList = new ArrayList<>();
        List<Long> auditUserIdList = new ArrayList<>();
        List<AuditVO> auditVOList = auditMapper.selectAuditByApplicationId(auditVO.getApplicationId());

        for(int i = 0; i < auditVOList.size(); i++){
            Long userId = auditVOList.get(i).getAuditUserId();
            auditUserIdList.add(userId);
            UserVO user = userMapper.selectById(userId);
            if (user != null) {
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