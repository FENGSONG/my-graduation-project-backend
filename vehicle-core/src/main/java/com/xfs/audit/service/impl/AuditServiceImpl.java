package com.xfs.audit.service.impl;

import com.xfs.application.mapper.ApplicationMapper;
import com.xfs.application.pojo.entity.Application;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/* @Transactional是Spring框架提供的一个用于管理事务的注解,用来管理类或方法上的事务行为
* 在对数据库做操作的时候,可以确保方法中的所有数据库操作都在同一个事务中执行,要么都成功,要么都失败 */
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

    @Override
    public void insertAudit(Application application) {
        log.debug("为当前申请单生成对应审批单的业务，参数：{}", application);
        //1.遍历审批人id集合 [106,103]
        List<Long> userIdList = application.getAuditUserIdList();
        for(int i = 0; i < userIdList.size(); i++){
            //2.循环几次,就代表有几个审批人id,就要生成几条审批数据
            Audit audit = new Audit();
            //3.设置本条审批单的相关数据
            audit.setApplicationId(application.getId());//申请单id
            audit.setAuditUserId(userIdList.get(i));//审批人id
            audit.setAuditSort(i);//审批顺序 0 1
            audit.setCreateTime(new Date());//创建时间
            if(i == 0){//如果是第一条审批单,审批状态为"待我审核"
                audit.setAuditStatus(AuditStatusEnum.MY_PENDING.getCode());
            }else{//如果不是第一条审批单,审批状态为"待他人审核"
                audit.setAuditStatus(AuditStatusEnum.PENDING.getCode());
            }
            auditMapper.insert(audit);//4.插入准备好的审批单数据
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
        BeanUtils.copyProperties(auditSaveParam, audit);//这里也包括前端传过来的审批状态30 40
        audit.setUpdateTime(new Date());//更新时间
        auditMapper.update(audit);

        //准备当前要更新的申请单对象
        Application application = new Application();
        application.setId(audit.getApplicationId());
        application.setUpdateTime(new Date());

        //判断前端传过来的操作是通过还是驳回,分头处理
        if(audit.getAuditStatus().equals(AuditStatusEnum.AUDITED.getCode())){//通过处理
            /* 继续查其它审批单:其它审批单与当前审批单,批同一个申请单 */
            AuditQuery auditQuery = new AuditQuery();
            auditQuery.setApplicationId(audit.getApplicationId());
            //根据申请单id查询批此申请单的所有未审批的审批单总数
            Integer count = auditMapper.selectRestAuditCount(auditQuery);
            if(count > 0){//还有未审批的审批单
                //继续封装下一个审批单的查询条件:批同一个申请单,且为刚刚审批单的次序+1
                auditQuery.setAuditSort(audit.getAuditSort() + 1);
                List<AuditVO> auditVOList = auditMapper.selectAudit(auditQuery);
                if(auditVOList != null && auditVOList.size() > 0){
                    AuditVO auditVO = auditVOList.get(0);
                    //创建第2个审批单对象,用于数据库更新
                    Audit audit2 = new Audit();
                    audit2.setId(auditVO.getId());
                    audit2.setAuditStatus(AuditStatusEnum.MY_PENDING.getCode());
                    audit2.setUpdateTime(new Date());
                    auditMapper.update(audit2);
                }
                //还需要设置申请单为审核中
                application.setStatus(ApplicationStatusEnum.AUDIT.getCode());
                applicationMapper.update(application);
            }else{//没有未审批的审批单了
                application.setStatus(ApplicationStatusEnum.AUDITED.getCode());
                applicationMapper.update(application);
            }
        }else if(audit.getAuditStatus().equals(AuditStatusEnum.REJECT.getCode())){//驳回处理
            AuditQuery auditQuery = new AuditQuery();
            auditQuery.setApplicationId(audit.getApplicationId());
            List<AuditVO> auditVOList = auditMapper.selectAudit(auditQuery);
            if(auditVOList != null && auditVOList.size() > 0){
                for(int i = 0;i < auditVOList.size();i++){
                    AuditVO auditVO = auditVOList.get(i);
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
        List<String> auditUsernameList = new ArrayList<>();//创建一个用来装审批人姓名的空集合
        List<Long> auditUserIdList = new ArrayList<>();//创建一个用来装审批人id的空集合
        //根据审批单VO中的申请单id,查询当前审批单对应的申请单下的所有审批单数据
        List<AuditVO> auditVOList =
                auditMapper.selectAuditByApplicationId(auditVO.getApplicationId());
        //遍历每一个审批单,获取每个审批单中的审批人id与姓名
        for(int i = 0;i <auditVOList.size();i++){
            Long userId = auditVOList.get(i).getAuditUserId();//获取当前遍历到的审批单的审批人id
            auditUserIdList.add(userId);//将当前的审批人id装到上面的id空集合中
            UserVO user = userMapper.selectById(userId);//根据用户id查出用户VO
            auditUsernameList.add(user.getUsername());//将当前的审批人姓名装到上面的name空集合中
        }
        StringJoiner stringJoiner = new StringJoiner(",");//准备拼接工具
        for (String username : auditUsernameList){//遍历审批人姓名集合并进行拼接
            stringJoiner.add(username);
        }
        auditVO.setAuditUserIdList(auditUserIdList);//将审批人id数据赋值给审批单VO
        auditVO.setAuditUsernameList(stringJoiner.toString());//将审批人姓名字符串赋值给审批单VO
    }
}
