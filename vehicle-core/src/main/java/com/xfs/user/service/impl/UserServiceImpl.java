package com.xfs.user.service.impl;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.base.util.AuthTokenUtil;
import com.xfs.base.util.PasswordUtil;
import com.xfs.org.mapper.OrgMapper;
import com.xfs.org.pojo.vo.OrgVO;
import com.xfs.role.mapper.RoleMapper;
import com.xfs.role.pojo.vo.RoleVO;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    OrgMapper orgMapper;
    @Autowired
    RoleMapper roleMapper;

    @Override
    public UserVO login(UserLoginParam userLoginParam) {
        log.debug("用户登录业务:userLoginParam={}", userLoginParam);
        String loginAccount = resolveLoginAccount(userLoginParam);
        UserVO userVO = userMapper.selectByAccount(loginAccount);
        if (userVO == null) {
            throw new ServiceException(StatusCode.USERNAME_ERROR);
        }
        if (!Objects.equals(String.valueOf(userVO.getPassword()), userLoginParam.getPassword())) {
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        if (!"1".equals(String.valueOf(userVO.getStatus()))) {
            throw new ServiceException(StatusCode.FORBIDDEN);
        }
        userVO.setToken(AuthTokenUtil.generateToken(userVO.getId()));
        userVO.setDataScope(normalizeDataScope(userVO));
        userVO.setMenuPermList(parseMenuPerms(userVO.getMenuPerms()));
        return userVO;
    }

    @Override
    public List<UserVO> selectUser(UserQuery userQuery) {
        log.debug("用户查询业务:userQuery={}", userQuery);
        Long loginUserId = AuthTokenUtil.getCurrentUserId();
        UserVO loginUser = userMapper.selectById(loginUserId);
        if (loginUser == null) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }

        UserQuery query = userQuery == null ? new UserQuery() : userQuery;
        query.setCurrentUserId(loginUserId);
        query.setCurrentEnterpriseId(loginUser.getEnterpriseId());
        query.setCurrentCompanyId(loginUser.getCompanyId());
        query.setCurrentDeptId(loginUser.getDeptId());
        query.setDataScope(normalizeDataScope(loginUser));

        List<UserVO> list = userMapper.selectUser(query);
        for (UserVO item : list) {
            item.setDataScope(normalizeDataScope(item));
            item.setMenuPermList(parseMenuPerms(item.getMenuPerms()));
        }
        return list;
    }

    @Override
    public void saveUser(UserSaveParam userSaveParam) {
        log.debug("用户保存业务:userSaveParam={}", userSaveParam);
        UserVO existedByUsername = userMapper.selectByUsername(userSaveParam.getUsername());
        if (userSaveParam.getId() == null) {
            if (existedByUsername != null) {
                throw new ServiceException(StatusCode.USERNAME_ALREADY_EXISTS);
            }
        } else if (existedByUsername != null && !Objects.equals(existedByUsername.getId(), userSaveParam.getId())) {
            throw new ServiceException(StatusCode.USERNAME_ALREADY_EXISTS);
        }

        User user = new User();
        BeanUtils.copyProperties(userSaveParam, user);

        UserVO existedUser = null;
        if (user.getId() != null) {
            existedUser = userMapper.selectById(user.getId());
            if (existedUser == null) {
                throw new ServiceException(StatusCode.DATA_UNEXISTS);
            }
        }

        String roleCode = String.valueOf(userSaveParam.getRoleCode() == null ? "" : userSaveParam.getRoleCode()).trim();
        if (roleCode.isEmpty() && existedUser != null) {
            roleCode = String.valueOf(existedUser.getRoleCode() == null ? "" : existedUser.getRoleCode()).trim();
        }
        if (!roleCode.isEmpty()) {
            RoleVO roleVO = roleMapper.selectByCode(roleCode);
            if (roleVO == null || !"1".equals(String.valueOf(roleVO.getStatus()))) {
                throw new ServiceException(StatusCode.VALIDATE_ERROR);
            }
            user.setRoleCode(roleCode);
        }

        if (userSaveParam.getOrgId() != null && userSaveParam.getOrgId() > 0) {
            fillOrgScope(user, userSaveParam.getOrgId());
        } else if (existedUser != null) {
            user.setOrgId(existedUser.getOrgId());
            user.setEnterpriseId(existedUser.getEnterpriseId());
            user.setCompanyId(existedUser.getCompanyId());
            user.setDeptId(existedUser.getDeptId());
        }

        if (user.getId() == null) {
            user.setPassword("123456");
            user.setCreateTime(new Date());
            userMapper.insert(user);
        } else {
            user.setUpdateTime(new Date());
            userMapper.update(user);
        }
    }

    @Override
    public void resetPassword(Long userId) {
        log.debug("重置密码业务:userId={}", userId);
        User user = new User();
        user.setId(userId);
        user.setPassword(PasswordUtil.generateRandomPassword(7));
        log.debug("新密码:{}", user.getPassword());
        user.setUpdateTime(new Date());
        userMapper.update(user);
    }

    @Override
    public void updateStatus(Long userId, String status) {
        log.debug("更新用户状态业务:userId={},status={}", userId, status);
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        user.setUpdateTime(new Date());
        userMapper.update(user);
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("删除用户业务:userId={}", userId);
        userMapper.deleteById(userId);
    }

    @Override
    public List<UserVO> selectAuditUserList(Long parentId) {
        log.debug("查询审批人列表业务:parentId={}", parentId);
        ArrayList<UserVO> userVOList = new ArrayList<>();
        UserVO auditUser1 = userMapper.selectById(parentId);
        if (auditUser1 != null) {
            userVOList.add(auditUser1);
        }
        if (auditUser1 != null && auditUser1.getParentId() != null) {
            UserVO auditUser2 = userMapper.selectById(auditUser1.getParentId());
            if (auditUser2 != null) {
                userVOList.add(auditUser2);
            }
        }
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

    private String resolveLoginAccount(UserLoginParam userLoginParam) {
        String account = String.valueOf(userLoginParam.getAccount() == null ? "" : userLoginParam.getAccount()).trim();
        if (!account.isEmpty()) {
            return account;
        }
        String username = String.valueOf(userLoginParam.getUsername() == null ? "" : userLoginParam.getUsername()).trim();
        if (!username.isEmpty()) {
            return username;
        }
        throw new ServiceException(StatusCode.VALIDATE_ERROR);
    }

    private String normalizeDataScope(UserVO userVO) {
        if (userVO == null) {
            return "SELF";
        }
        if ("99".equals(String.valueOf(userVO.getLevel()))) {
            return "ALL";
        }
        String dataScope = String.valueOf(userVO.getDataScope() == null ? "" : userVO.getDataScope()).trim().toUpperCase(Locale.ROOT);
        if ("ALL".equals(dataScope)
                || "ENTERPRISE".equals(dataScope)
                || "COMPANY".equals(dataScope)
                || "DEPT".equals(dataScope)
                || "SELF".equals(dataScope)) {
            return dataScope;
        }
        return "SELF";
    }

    private List<String> parseMenuPerms(String menuPerms) {
        if (menuPerms == null || menuPerms.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(menuPerms.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private void fillOrgScope(User user, Long orgId) {
        OrgVO orgVO = orgMapper.selectById(orgId);
        if (orgVO == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
        user.setOrgId(orgId);
        if (orgVO.getOrgLevel() == 1) {
            user.setEnterpriseId(orgVO.getId());
            user.setCompanyId(null);
            user.setDeptId(null);
            return;
        }
        if (orgVO.getOrgLevel() == 2) {
            user.setEnterpriseId(orgVO.getEnterpriseId());
            user.setCompanyId(orgVO.getId());
            user.setDeptId(null);
            return;
        }
        user.setEnterpriseId(orgVO.getEnterpriseId());
        user.setCompanyId(orgVO.getCompanyId());
        user.setDeptId(orgVO.getId());
    }
}
