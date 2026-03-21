package com.xfs.user.service.impl;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.base.util.AuthTokenUtil;
import com.xfs.base.util.PasswordUtil;
import com.xfs.user.mapper.UserMapper;
import com.xfs.user.pojo.dto.UserLoginParam;
import com.xfs.user.pojo.dto.UserPasswordChangeParam;
import com.xfs.user.pojo.dto.UserProfileUpdateParam;
import com.xfs.user.pojo.dto.UserQuery;
import com.xfs.user.pojo.dto.UserSaveParam;
import com.xfs.user.pojo.entity.User;
import com.xfs.user.pojo.vo.UserVO;
import com.xfs.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service //表明当前类为业务层实现类, 交给Spring管理并被识别到,方便注入
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public UserVO login(UserLoginParam userLoginParam) {
        log.debug("用户登录业务:userLoginParam={}",userLoginParam);
        UserVO userVO = userMapper.selectByUsername(userLoginParam.getUsername());
        /* 如果业务层遇到了问题,不能直接返回结果给前端,而是要抛出业务层异常,让全局异常处理器来处理
         * 一旦抛出异常,结果是由全局异常处理器来返回给前端的,就不走controller了 */
        if(userVO == null){
            throw new ServiceException(StatusCode.USERNAME_ERROR);
        }
        if(!userVO.getPassword().equals(userLoginParam.getPassword())){
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        // P1: 禁用账号不可登录（通用状态：1=启用，0=禁用）
        if (!"1".equals(String.valueOf(userVO.getStatus()))) {
            throw new ServiceException(StatusCode.FORBIDDEN);
        }
        userVO.setToken(AuthTokenUtil.generateToken(userVO.getId()));
        log.debug("用户查询结果:userVO={}",userVO);
        return userVO;
    }

    @Override
    public List<UserVO> selectUser(UserQuery userQuery) {
        log.debug("用户查询业务:userQuery={}",userQuery);
        List<UserVO> list = userMapper.selectUser(userQuery);
        return list;
    }

    @Override
    public void saveUser(UserSaveParam userSaveParam) {
        log.debug("用户保存业务:userSaveParam={}",userSaveParam);
        User user = new User();
        BeanUtils.copyProperties(userSaveParam,user);
        if(user.getId()==null){//新增
            user.setPassword("123456");
            user.setCreateTime(new Date());
            userMapper.insert(user);
        }else{//更新
            user.setUpdateTime(new Date());
            userMapper.update(user);
        }
    }

    @Override
    public void resetPassword(Long userId) {
        log.debug("重置密码业务:userId={}",userId);
        User user = new User();
        user.setId(userId);
        //user.setPassword("root");
        //调用密码工具生成一个随机的7位密码
        user.setPassword(PasswordUtil.generateRandomPassword(7));
        log.debug("新密码:{}",user.getPassword());
        user.setUpdateTime(new Date());
        userMapper.update(user);
    }

    @Override
    public void updateStatus(Long userId, String status) {
        log.debug("更新用户状态业务:userId={},status={}",userId,status);
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        user.setUpdateTime(new Date());
        userMapper.update(user);
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("删除用户业务:userId={}",userId);
        userMapper.deleteById(userId);
    }

    @Override
    public List<UserVO> selectAuditUserList(Long parentId) {
        log.debug("查询审批人列表业务:parentId={}",parentId);
        //1.准备一个空集合,用来保存查出来的多个审批人
        ArrayList<UserVO> userVOList = new ArrayList<>();
        //2.先根据传过来的直属领导id,将当前登录人的直属领导查出来
        UserVO auditUser1 = userMapper.selectById(parentId);
        //3.将当前登录人添加到审批人列表中
        userVOList.add(auditUser1);
        //4.如果有直属领导,且直属领导还有领导,继续查
        if(auditUser1 != null && auditUser1.getParentId() != null){
            UserVO auditUser2 = userMapper.selectById(auditUser1.getParentId());
            //5.将查出来的领导添加到审批人列表中
            userVOList.add(auditUser2);
        }
        //6.将准备好的审批人集合返回给上一层
        return userVOList;
    }

    @Override
    public void updateSelfProfile(Long userId, UserProfileUpdateParam userProfileUpdateParam) {
        log.debug("自助更新个人资料业务:userId={},param={}", userId, userProfileUpdateParam);
        UserVO currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }

        User user = new User();
        user.setId(userId);
        user.setPhone(userProfileUpdateParam.getPhone());
        user.setEmail(userProfileUpdateParam.getEmail());
        user.setAge(userProfileUpdateParam.getAge());
        user.setGender(userProfileUpdateParam.getGender());
        user.setUpdateTime(new Date());
        userMapper.update(user);
    }

    @Override
    public void changeSelfPassword(Long userId, UserPasswordChangeParam userPasswordChangeParam) {
        log.debug("自助修改密码业务:userId={}", userId);
        UserVO currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }
        if (!String.valueOf(currentUser.getPassword()).equals(userPasswordChangeParam.getOldPassword())) {
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        if (userPasswordChangeParam.getOldPassword().equals(userPasswordChangeParam.getNewPassword())) {
            throw new ServiceException(StatusCode.VALIDATE_ERROR);
        }

        User user = new User();
        user.setId(userId);
        user.setPassword(userPasswordChangeParam.getNewPassword());
        user.setUpdateTime(new Date());
        userMapper.update(user);
    }
}
