package com.xfs.user.service;

import com.xfs.user.pojo.dto.UserLoginParam;
import com.xfs.user.pojo.dto.UserQuery;
import com.xfs.user.pojo.dto.UserSaveParam;
import com.xfs.user.pojo.vo.UserVO;

import java.util.List;

public interface UserService {
    UserVO login(UserLoginParam userLoginParam);

    List<UserVO> selectUser(UserQuery userQuery);

    void saveUser(UserSaveParam userSaveParam);

    void resetPassword(Long userId);

    void updateStatus(Long userId, String status);

    void deleteUser(Long userId);

    List<UserVO> selectAuditUserList(Long parentId);
}
