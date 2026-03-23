package com.xfs.org.service.impl;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.org.mapper.OrgMapper;
import com.xfs.org.pojo.dto.OrgQuery;
import com.xfs.org.pojo.dto.OrgSaveParam;
import com.xfs.org.pojo.entity.Org;
import com.xfs.org.pojo.vo.OrgVO;
import com.xfs.org.service.OrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Slf4j
public class OrgServiceImpl implements OrgService {
    @Autowired
    private OrgMapper orgMapper;

    @Override
    public List<OrgVO> selectOrg(OrgQuery orgQuery) {
        return orgMapper.selectOrg(orgQuery);
    }

    @Override
    public void saveOrg(OrgSaveParam orgSaveParam) {
        log.debug("保存组织业务，参数：{}", orgSaveParam);
        String orgType = normalizeOrgType(orgSaveParam.getOrgType());
        Long parentId = orgSaveParam.getParentId() == null ? 0L : orgSaveParam.getParentId();

        OrgVO parent = null;
        int orgLevel;
        if (parentId <= 0) {
            orgLevel = 1;
            if (!"HQ".equals(orgType)) {
                throw new ServiceException(StatusCode.VALIDATE_ERROR);
            }
        } else {
            parent = orgMapper.selectById(parentId);
            if (parent == null) {
                throw new ServiceException(StatusCode.DATA_UNEXISTS);
            }
            orgLevel = parent.getOrgLevel() + 1;
            if (orgLevel > 3) {
                throw new ServiceException(StatusCode.VALIDATE_ERROR);
            }
            if (orgLevel == 2 && !"COMPANY".equals(orgType)) {
                throw new ServiceException(StatusCode.VALIDATE_ERROR);
            }
            if (orgLevel == 3 && !"DEPT".equals(orgType)) {
                throw new ServiceException(StatusCode.VALIDATE_ERROR);
            }
        }

        Org org = new Org();
        BeanUtils.copyProperties(orgSaveParam, org);
        org.setOrgType(orgType);
        org.setParentId(parentId <= 0 ? 0L : parentId);
        org.setOrgLevel(orgLevel);
        org.setSort(orgSaveParam.getSort() == null ? 10 : orgSaveParam.getSort());

        if (parent == null) {
            org.setEnterpriseId(null);
            org.setCompanyId(null);
        } else if (orgLevel == 2) {
            org.setEnterpriseId(parent.getEnterpriseId() != null ? parent.getEnterpriseId() : parent.getId());
            org.setCompanyId(null);
        } else {
            org.setEnterpriseId(parent.getEnterpriseId());
            org.setCompanyId(parent.getOrgLevel() == 2 ? parent.getId() : parent.getCompanyId());
        }

        if (org.getId() == null) {
            org.setCreateTime(new Date());
            orgMapper.insert(org);
            // 总部节点企业ID取自身ID
            if (org.getOrgLevel() == 1) {
                Org patch = new Org();
                patch.setId(org.getId());
                patch.setEnterpriseId(org.getId());
                patch.setUpdateTime(new Date());
                orgMapper.update(patch);
            }
        } else {
            OrgVO existed = orgMapper.selectById(org.getId());
            if (existed == null) {
                throw new ServiceException(StatusCode.DATA_UNEXISTS);
            }
            if (!Objects.equals(existed.getParentId(), org.getParentId()) && existed.getOrgLevel() == 1) {
                throw new ServiceException(StatusCode.VALIDATE_ERROR);
            }
            org.setUpdateTime(new Date());
            orgMapper.update(org);
        }
    }

    @Override
    public void deleteOrg(Long id) {
        log.debug("删除组织业务，参数：{}", id);
        OrgVO org = orgMapper.selectById(id);
        if (org == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
        Integer childCount = orgMapper.countChildren(id);
        if (childCount != null && childCount > 0) {
            throw new ServiceException(StatusCode.OPERATION_FAILED);
        }
        orgMapper.deleteById(id);
    }

    private String normalizeOrgType(String orgType) {
        String normalized = String.valueOf(orgType == null ? "" : orgType).trim().toUpperCase(Locale.ROOT);
        if ("HQ".equals(normalized) || "COMPANY".equals(normalized) || "DEPT".equals(normalized)) {
            return normalized;
        }
        throw new ServiceException(StatusCode.VALIDATE_ERROR);
    }
}
