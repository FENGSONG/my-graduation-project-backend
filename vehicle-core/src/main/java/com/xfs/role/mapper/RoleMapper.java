package com.xfs.role.mapper;

import com.xfs.role.pojo.dto.RoleQuery;
import com.xfs.role.pojo.entity.Role;
import com.xfs.role.pojo.vo.RoleVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper {
    List<RoleVO> selectRole(RoleQuery roleQuery);

    RoleVO selectByCode(String roleCode);

    RoleVO selectById(Long id);

    void insert(Role role);

    void update(Role role);

    void deleteById(Long id);
}
