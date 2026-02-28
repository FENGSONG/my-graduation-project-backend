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

        /* 遇到的问题:新增申请单对应的审批单时,审批单数据没有此申请单id
        原因:执行insert方法的SQL时,并没有把刚刚生成的申请单id回填到application对象中
        解决办法:给此SQL上加useGeneratedKeys="true" keyProperty="id"属性
        效果:JDBC自动回填此申请单id到application对象的id属性中,再传给审批,审批就拿到申请单id了*/
        applicationMapper.insert(application);

        //生成申请单后,要为此申请单生成对应的审批单
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
        applicationMapper.back(application);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setStatus("1");//空闲
        vehicle.setUpdateTime(new Date());
        vehicleMapper.update(vehicle);
    }

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
            auditUsernameList.add(userVO.getUsername());//将审批人姓名存入空集合中
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

