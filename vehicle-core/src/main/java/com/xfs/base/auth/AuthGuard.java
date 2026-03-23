package com.xfs.base.auth;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.base.util.AuthTokenUtil;
import com.xfs.user.mapper.UserMapper;
import com.xfs.user.pojo.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthGuard {
    @Autowired
    private UserMapper userMapper;

    public UserVO requireLoginUser() {
        Long userId = AuthTokenUtil.getCurrentUserId();
        UserVO loginUser = userMapper.selectById(userId);
        if (loginUser == null) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }
        return loginUser;
    }

    public UserVO requireDispatcher() {
        UserVO loginUser = requireLoginUser();
        if (!"99".equals(String.valueOf(loginUser.getLevel()))) {
            throw new ServiceException(StatusCode.FORBIDDEN);
        }
        return loginUser;
    }

    public UserVO requireMenuPerm(String permCode) {
        UserVO loginUser = requireLoginUser();
        if (loginUser == null) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }
        // 兼容超级调度员
        if ("99".equals(String.valueOf(loginUser.getLevel()))) {
            return loginUser;
        }
        String perms = String.valueOf(loginUser.getMenuPerms() == null ? "" : loginUser.getMenuPerms()).trim();
        if (perms.isEmpty()) {
            throw new ServiceException(StatusCode.FORBIDDEN);
        }
        String[] permArray = perms.split(",");
        for (String item : permArray) {
            String normalized = String.valueOf(item == null ? "" : item).trim();
            if (normalized.isEmpty()) {
                continue;
            }
            if ("*".equals(normalized) || normalized.equals(permCode)) {
                return loginUser;
            }
        }
        throw new ServiceException(StatusCode.FORBIDDEN);
    }
}
