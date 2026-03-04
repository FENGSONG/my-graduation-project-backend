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
import com.xfs.user.mapper.UserMapper;
import com.xfs.user.pojo.vo.UserVO;
import com.xfs.vehicle.mapper.VehicleMapper;
import com.xfs.vehicle.pojo.entity.Vehicle;
import com.xfs.vehicle.pojo.vo.VehicleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        Application application = new Application();
        BeanUtils.copyProperties(applicationSaveParam, application);
        application.setStatus(ApplicationStatusEnum.PENDING.getCode());
        application.setCreateTime(new Date());

        // ================== 核心修复：防空指针 + 全自动动态找领导 ==================
        List<Long> dynamicAuditUserIds = new ArrayList<>(); // 实例化集合，确保绝对不为 null
        Long currentUserId = application.getUserId();

        // 1. 尝试根据当前申请人，动态向上找直属领导
        if (currentUserId != null) {
            UserVO currentUser = userMapper.selectById(currentUserId);
            if (currentUser != null && currentUser.getParentId() != null) {
                Long parentId = currentUser.getParentId();
                int maxDepth = 5; // 防护机制：最多向上找 5 级领导，防止数据脏乱导致死循环

                while (parentId != null && parentId != 0 && maxDepth > 0) {
                    dynamicAuditUserIds.add(parentId); // 将找到的领导 ID 加入审批列表

                    // 继续查这位领导，看他是否还有上级
                    UserVO parentUser = userMapper.selectById(parentId);
                    parentId = (parentUser != null) ? parentUser.getParentId() : null;
                    maxDepth--;
                }
            }
        }

        // 2. 【安全兜底】：如果该员工没有上级，或者没查到数据，给一个默认审批人，坚决不传空集合！
        if (dynamicAuditUserIds.isEmpty()) {
            log.warn("未找到用户 {} 的直属领导，启用默认兜底审批人", currentUserId);
            // 随便塞一个默认的审批人ID进去，假设 2 号用户是超级管理员或经理
            dynamicAuditUserIds.add(2L);
        }

        // 将组装好的、绝对不可能为空的审批人列表放进 application 对象
        application.setAuditUserIdList(dynamicAuditUserIds);
        // ====================================================================

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
        Application application = new Application();
        application.setId(id);
        application.setStatus(ApplicationStatusEnum.CANCEL.getCode());
        application.setUpdateTime(new Date());
        applicationMapper.update(application);

        //还需要删除此申请单对应的所有审批单
        auditMapper.deleteByApplicationId(id);
    }

    @Override
    public void distribute(Long applicationId, Long vehicleId) {
        log.debug("分配车辆业务参数:申请单编号:{},车辆编号:{}",applicationId,vehicleId);
        Application application = new Application();
        application.setId(applicationId);
        application.setVehicleId(vehicleId);
        application.setStatus(ApplicationStatusEnum.ALLOCATION.getCode());
        application.setUpdateTime(new Date());
        applicationMapper.update(application);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setStatus("2");//占用
        vehicle.setUpdateTime(new Date());
        vehicleMapper.update(vehicle);
    }

    @Override
    public void back(Long applicationId, Long vehicleId) {
        log.debug("还车业务参数:申请单编号:{},车辆编号:{}",applicationId,vehicleId);
        Application application = new Application();
        application.setId(applicationId);
        application.setVehicleId(null);
        application.setStatus(ApplicationStatusEnum.END.getCode());
        application.setUpdateTime(new Date());

        // 【已修改】：这里改用 applicationMapper.back，触发XML中把 vehicle_id 置空的特定逻辑
        applicationMapper.back(application);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setStatus("1");//空闲
        vehicle.setUpdateTime(new Date());
        vehicleMapper.update(vehicle);
    }

    // ================== 新增核心方法：自动调度分配 ==================
    @Override
    public boolean autoDistribute(Long applicationId) {
        log.debug("触发系统自动分配车辆机制，申请单编号:{}", applicationId);

        Application application = applicationMapper.selectById(applicationId);

        if (application == null || application.getStartTime() == null || application.getEndTime() == null) {
            log.error("申请单不存在或起止时间为空，无法进行时间段排期检测");
            return false;
        }

        List<VehicleVO> availableVehicles = vehicleMapper.findAvailableVehicles(application.getStartTime(), application.getEndTime());

        if (availableVehicles != null && !availableVehicles.isEmpty()) {
            VehicleVO selectedVehicle = availableVehicles.get(0);
            log.info("自动找车成功！为您分配车辆ID: {}, 车牌号: {}", selectedVehicle.getId(), selectedVehicle.getLicense());

            this.distribute(applicationId, selectedVehicle.getId());
            return true;
        } else {
            log.warn("申请单编号 {} 的指定时间段内暂无可用车辆，转入人工排队调度状态", applicationId);
            return false;
        }
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
}