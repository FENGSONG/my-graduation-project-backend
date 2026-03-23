package com.xfs.org.service;

import com.xfs.org.pojo.dto.OrgQuery;
import com.xfs.org.pojo.dto.OrgSaveParam;
import com.xfs.org.pojo.vo.OrgVO;

import java.util.List;

public interface OrgService {
    List<OrgVO> selectOrg(OrgQuery orgQuery);

    void saveOrg(OrgSaveParam orgSaveParam);

    void deleteOrg(Long id);
}
