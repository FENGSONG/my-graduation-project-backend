package com.xfs.application.service.impl;

import com.xfs.application.mapper.ApplicationMapper;
import com.xfs.application.pojo.dto.ApplicationQuery;
import com.xfs.application.pojo.dto.ApplicationSaveParam;
import com.xfs.application.pojo.entity.Application;
import com.xfs.application.pojo.vo.ApplicationVO;
import com.xfs.application.service.ApplicationService;
import com.xfs.audit.mapper.AuditMapper;
import com.xfs.audit.pojo.vo.AuditVO;
import com.xfs.audit.service.AuditService;
import com.xfs.base.enums.ApplicationStatusEnum;
import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.base.util.AuthTokenUtil;
import com.xfs.user.mapper.UserMapper;
import com.xfs.user.pojo.dto.UserQuery;
import com.xfs.user.pojo.vo.UserVO;
import com.xfs.vehicle.mapper.VehicleMapper;
import com.xfs.vehicle.pojo.vo.VehicleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

@Transactional
@Service
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    ApplicationMapper applicationMapper;
    @Autowired
    AuditService auditService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    AuditMapper auditMapper;
    @Autowired
    VehicleMapper vehicleMapper;

    @Override
    public void save(ApplicationSaveParam applicationSaveParam) {
        log.debug("保存申请业务，参数：{}", applicationSaveParam);
        Long loginUserId = AuthTokenUtil.getCurrentUserId();
        UserVO loginUser = userMapper.selectById(loginUserId);
        if (loginUser == null) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }

        if (applicationSaveParam.getStartTime() == null || applicationSaveParam.getEndTime() == null
                || !applicationSaveParam.getStartTime().before(applicationSaveParam.getEndTime())) {
            throw new IllegalArgumentException("用车开始时间必须早于结束时间");
        }

        Application application = new Application();
        BeanUtils.copyProperties(applicationSaveParam, application);
        // 后端强制以登录人身份落库，不信任前端 userId/username
        application.setUserId(loginUserId);
        application.setUsername(loginUser.getUsername());
        application.setStatus(ApplicationStatusEnum.PENDING.getCode());
        application.setCreateTime(new Date());

        // P2: 构建更稳健的审批链（防循环、过滤禁用账号、移除硬编码审批人）
        List<Long> dynamicAuditUserIds = buildAuditChain(loginUserId, loginUser);
        application.setAuditUserIdList(dynamicAuditUserIds);

        /* 遇到的问题:新增申请单对应的审批单时,审批单数据没有此申请单id
        原因:执行insert方法的SQL时,并没有把刚刚生成的申请单id回填到application对象中
        解决办法:给此SQL上加useGeneratedKeys="true" keyProperty="id"属性
        效果:JDBC自动回填此申请单id到application对象的id属性中,再传给审批,审批就拿到申请单id了*/
        applicationMapper.insert(application);

        // 生成申请单后,要为此申请单生成对应的审批单（此时集合安全，不再报 NullPointerException）
        auditService.insertAudit(application);
    }

    @Override
    public List<ApplicationVO> selectApplication(ApplicationQuery applicationQuery) {
        log.debug("查询申请单列表业务参数：{}", applicationQuery);
        Long loginUserId = AuthTokenUtil.getCurrentUserId();
        UserVO loginUser = userMapper.selectById(loginUserId);
        if (loginUser == null) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }
        if (applicationQuery == null) {
            applicationQuery = new ApplicationQuery();
        }
        // P1: 非调度员只能看自己的申请单，避免前端越权查询
        if (!isDispatcher(loginUser)) {
            applicationQuery.setUserId(loginUserId);
        }

        List<ApplicationVO> list = applicationMapper.selectApplication(applicationQuery);
        //遍历得到每一个申请单VO,为其补全审批人数据
        for(int i = 0; i < list.size(); i++){
            ApplicationVO applicationVO = list.get(i);
            //创建一个自定义方法为申请单VO补全审批人数据
            assignAuditUserList(applicationVO);
        }
        return list;
    }

    @Override
    public void cancel(Long id) {
        log.debug("撤销申请业务参数:{}",id);
        Long loginUserId = AuthTokenUtil.getCurrentUserId();
        Application current = applicationMapper.selectById(id);
        if (current == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
        if (!loginUserId.equals(current.getUserId())) {
            throw new ServiceException(StatusCode.FORBIDDEN);
        }
        if (!ApplicationStatusEnum.PENDING.getCode().equals(current.getStatus())) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }
        int rows = applicationMapper.cancelIfPendingAndUser(id, loginUserId, new Date());
        if (rows != 1) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }

        //还需要删除此申请单对应的所有审批单
        auditMapper.deleteByApplicationId(id);
    }

    @Override
    public void distribute(Long applicationId, Long vehicleId) {
        log.debug("分配车辆业务参数:申请单编号:{},车辆编号:{}",applicationId,vehicleId);
        // 分配动作必须由已登录用户触发
        AuthTokenUtil.getCurrentUserId();
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("车辆编号不合法");
        }

        Application current = applicationMapper.selectById(applicationId);
        if (current == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
        if (!ApplicationStatusEnum.AUDITED.getCode().equals(current.getStatus())) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }

        if (current.getStartTime() == null || current.getEndTime() == null) {
            throw new IllegalArgumentException("申请单时间范围不存在");
        }

        // 必须是当前时间段内可排班车辆，防止绕开审批流直接占用
        List<VehicleVO> availableVehicles = vehicleMapper.findAvailableVehicles(current.getStartTime(), current.getEndTime());
        boolean canUse = false;
        if (availableVehicles != null) {
            for (VehicleVO item : availableVehicles) {
                if (item != null && vehicleId.equals(item.getId())) {
                    canUse = true;
                    break;
                }
            }
        }
        if (!canUse) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }

        Application application = new Application();
        application.setId(applicationId);
        application.setVehicleId(vehicleId);
        application.setStatus(ApplicationStatusEnum.ALLOCATION.getCode());
        application.setUpdateTime(new Date());
        int appRows = applicationMapper.updateIfStatus(application, ApplicationStatusEnum.AUDITED.getCode());
        if (appRows != 1) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }

        int vehicleRows = vehicleMapper.updateStatusIfMatch(vehicleId, "1", "2", new Date());
        if (vehicleRows != 1) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }
    }

    @Override
    public void back(Long applicationId, Long vehicleId) {
        log.debug("还车业务参数:申请单编号:{},车辆编号:{}",applicationId,vehicleId);
        Long loginUserId = AuthTokenUtil.getCurrentUserId();

        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("车辆编号不合法");
        }

        Application current = applicationMapper.selectById(applicationId);
        if (current == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
        if (!loginUserId.equals(current.getUserId())) {
            throw new ServiceException(StatusCode.FORBIDDEN);
        }
        if (!ApplicationStatusEnum.ALLOCATION.getCode().equals(current.getStatus())) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }
        if (current.getVehicleId() == null || !vehicleId.equals(current.getVehicleId())) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }

        Application application = new Application();
        application.setId(applicationId);
        application.setStatus(ApplicationStatusEnum.END.getCode());
        application.setUpdateTime(new Date());
        int appRows = applicationMapper.backIfStatus(application, ApplicationStatusEnum.ALLOCATION.getCode());
        if (appRows != 1) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }

        int vehicleRows = vehicleMapper.updateStatusIfMatch(vehicleId, "2", "1", new Date());
        if (vehicleRows != 1) {
            throw new ServiceException(StatusCode.ILLEGAL_STATUS);
        }
    }

    // ================== 新增核心方法：自动调度分配 ==================
    @Override
    public boolean autoDistribute(Long applicationId) {
        log.debug("触发系统自动分配车辆机制，申请单编号:{}", applicationId);

        Application application = applicationMapper.selectById(applicationId);

        if (application == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }

        if (!ApplicationStatusEnum.AUDITED.getCode().equals(application.getStatus())) {
            log.warn("申请单 {} 当前状态={}，不是已通过(50)，跳过自动分配", applicationId, application.getStatus());
            return false;
        }

        if (application.getStartTime() == null || application.getEndTime() == null) {
            log.error("申请单 {} 起止时间为空，无法进行时间段排期检测", applicationId);
            return false;
        }

        // 企业策略：优先按员工在大厅选择的“意向车辆”分配，失败再回退到自动找车
        Long preferredVehicleId = application.getVehicleId();
        if (preferredVehicleId != null && preferredVehicleId > 0) {
            try {
                this.distribute(applicationId, preferredVehicleId);
                log.info("优先分配成功！申请单 {} 命中意向车辆ID: {}", applicationId, preferredVehicleId);
                return true;
            } catch (ServiceException ex) {
                if (StatusCode.ILLEGAL_STATUS.equals(ex.getStatusCode())) {
                    log.warn("意向车辆当前不可分配，申请单 {}，车辆ID {}，进入回退自动找车",
                            applicationId, preferredVehicleId);
                } else {
                    throw ex;
                }
            }
        }

        List<VehicleVO> availableVehicles = vehicleMapper.findAvailableVehicles(application.getStartTime(), application.getEndTime());

        if (availableVehicles == null || availableVehicles.isEmpty()) {
            log.warn("申请单编号 {} 的指定时间段内暂无可用车辆，转入人工排队调度状态", applicationId);
            return false;
        }

        // 依次尝试分配，避免并发场景下第1辆车被瞬时占用导致整体失败
        for (VehicleVO selectedVehicle : availableVehicles) {
            if (selectedVehicle == null || selectedVehicle.getId() == null) {
                continue;
            }
            // 已经尝试过意向车辆，回退列表里跳过避免重复尝试
            if (preferredVehicleId != null && preferredVehicleId.equals(selectedVehicle.getId())) {
                continue;
            }
            try {
                this.distribute(applicationId, selectedVehicle.getId());
                log.info("自动找车成功！为申请单 {} 分配车辆ID: {}, 车牌号: {}",
                        applicationId, selectedVehicle.getId(), selectedVehicle.getLicense());
                return true;
            } catch (ServiceException ex) {
                // 车辆被并发占用/申请状态被并发变更时，继续尝试下一辆
                if (StatusCode.ILLEGAL_STATUS.equals(ex.getStatusCode())) {
                    log.warn("自动分配车辆冲突，申请单 {} 车辆 {} 重试下一辆",
                            applicationId, selectedVehicle.getId());
                    continue;
                }
                throw ex;
            }
        }

        log.warn("申请单编号 {} 所有候选车辆分配失败，转入人工排队调度状态", applicationId);
        return false;
    }
    // ================================================================

    /* 为申请单VO补全审批人信息,此方法并不会产生额外的申请单VO!
     * List<Long> auditUserIdList; //[106,103]
     * String auditUsernameList; //"moly,tom"
     */
    private void assignAuditUserList(ApplicationVO applicationVO) {
        List<Long> auditUserIdList = new ArrayList<>();//准备空集合用来存放审批人id
        List<String> auditUsernameList = new ArrayList<>();//准备空集合用来存放审批人姓名
        //获取此申请单对应的多条审批单数据
        List<AuditVO> auditVOList =auditService.selectAuditByApplicationId(applicationVO.getId());
        //遍历得到每条审批单数据
        for(int i = 0; i < auditVOList.size(); i++){
            //拿到当前遍历到的审批单数据
            AuditVO auditVO = auditVOList.get(i);
            //从审批人数据中获取审批人id
            Long id = auditVO.getAuditUserId();
            auditUserIdList.add(id);//添加到空集合中
            UserVO userVO = userMapper.selectById(id);//根据审批人id查询审批人VO
            if(userVO != null) {
                auditUsernameList.add(userVO.getUsername());//将审批人姓名存入空集合中
            }
        }
        //准备拼接工具,拼接的连接符号是逗号
        StringJoiner stringJoiner = new StringJoiner(",");
        //依次将姓名集合中的审批人姓名添加到拼接工具中
        for(String username : auditUsernameList){
            stringJoiner.add(username);
        }
        //为传入的申请单VO补全审批人id集合与审批人姓名字符串数据
        applicationVO.setAuditUserIdList(auditUserIdList);//[106,103]
        applicationVO.setAuditUsernameList(stringJoiner.toString());//moly,tom
    }

    private boolean isDispatcher(UserVO user) {
        return user != null && "99".equals(String.valueOf(user.getLevel()));
    }

    private List<Long> buildAuditChain(Long loginUserId, UserVO loginUser) {
        List<Long> chain = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        visited.add(loginUserId);

        Long parentId = loginUser.getParentId();
        int maxDepth = 8; // 防护机制：最多向上找 8 级，避免脏数据死循环
        while (parentId != null && parentId > 0 && maxDepth > 0) {
            if (!visited.add(parentId)) {
                log.warn("检测到审批链循环引用，登录人:{}, parentId:{}", loginUserId, parentId);
                break;
            }
            UserVO parentUser = userMapper.selectById(parentId);
            if (parentUser == null) {
                log.warn("审批链节点用户不存在，id:{}", parentId);
                break;
            }
            if ("1".equals(parentUser.getStatus())) {
                chain.add(parentId);
            } else {
                log.warn("审批链节点用户已禁用，自动跳过，id:{}", parentId);
            }
            parentId = parentUser.getParentId();
            maxDepth--;
        }

        if (!chain.isEmpty()) {
            return chain;
        }

        // 无直属审批链时，兜底到系统内启用中的调度员(99)
        UserQuery userQuery = new UserQuery();
        userQuery.setLevel("99");
        userQuery.setStatus("1");
        List<UserVO> dispatchers = userMapper.selectUser(userQuery);
        if (dispatchers != null) {
            for (UserVO dispatcher : dispatchers) {
                if (dispatcher != null && dispatcher.getId() != null && !dispatcher.getId().equals(loginUserId)) {
                    chain.add(dispatcher.getId());
                    break;
                }
            }
        }
        if (chain.isEmpty()) {
            log.error("未找到可用审批人，登录人:{}", loginUserId);
            throw new ServiceException(StatusCode.OPERATION_FAILED);
        }
        return chain;
    }
}
