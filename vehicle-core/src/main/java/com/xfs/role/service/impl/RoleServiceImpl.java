package com.xfs.role.service.impl;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.role.mapper.RoleMapper;
import com.xfs.role.pojo.dto.RoleQuery;
import com.xfs.role.pojo.dto.RoleSaveParam;
import com.xfs.role.pojo.entity.Role;
import com.xfs.role.pojo.vo.RoleVO;
import com.xfs.role.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<RoleVO> selectRole(RoleQuery roleQuery) {
        List<RoleVO> list = roleMapper.selectRole(roleQuery);
        for (RoleVO role : list) {
            role.setMenuPermList(parseMenuPerms(role.getMenuPerms()));
        }
        return list;
    }

    @Override
    public void saveRole(RoleSaveParam roleSaveParam) {
        log.debug("保存角色业务，参数：{}", roleSaveParam);
        String dataScope = normalizeDataScope(roleSaveParam.getDataScope());

        RoleVO existed = roleMapper.selectByCode(roleSaveParam.getRoleCode());
        if (roleSaveParam.getId() == null) {
            if (existed != null) {
                throw new ServiceException(StatusCode.OPERATION_FAILED);
            }
        } else if (existed != null && !Objects.equals(existed.getId(), roleSaveParam.getId())) {
            throw new ServiceException(StatusCode.OPERATION_FAILED);
        }

        Role role = new Role();
        BeanUtils.copyProperties(roleSaveParam, role);
        role.setDataScope(dataScope);
        role.setMenuPerms(normalizeMenuPerms(roleSaveParam.getMenuPerms()));

        if (role.getId() == null) {
            role.setCreateTime(new Date());
            roleMapper.insert(role);
        } else {
            role.setUpdateTime(new Date());
            roleMapper.update(role);
        }
    }

    @Override
    public void deleteRole(Long id) {
        RoleVO role = roleMapper.selectById(id);
        if (role == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
        roleMapper.deleteById(id);
    }

    private String normalizeDataScope(String dataScope) {
        String normalized = String.valueOf(dataScope == null ? "" : dataScope).trim().toUpperCase(Locale.ROOT);
        if ("ALL".equals(normalized)
                || "ENTERPRISE".equals(normalized)
                || "COMPANY".equals(normalized)
                || "DEPT".equals(normalized)
                || "SELF".equals(normalized)) {
            return normalized;
        }
        throw new ServiceException(StatusCode.VALIDATE_ERROR);
    }

    private String normalizeMenuPerms(String menuPerms) {
        if (menuPerms == null || menuPerms.trim().isEmpty()) {
            return "";
        }
        return Arrays.stream(menuPerms.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .distinct()
                .collect(Collectors.joining(","));
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
}
