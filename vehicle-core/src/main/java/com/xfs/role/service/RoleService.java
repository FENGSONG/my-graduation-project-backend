package com.xfs.role.service;

import com.xfs.role.pojo.dto.RoleQuery;
import com.xfs.role.pojo.dto.RoleSaveParam;
import com.xfs.role.pojo.vo.RoleVO;

import java.util.List;

public interface RoleService {
    List<RoleVO> selectRole(RoleQuery roleQuery);

    void saveRole(RoleSaveParam roleSaveParam);

    void deleteRole(Long id);
}
