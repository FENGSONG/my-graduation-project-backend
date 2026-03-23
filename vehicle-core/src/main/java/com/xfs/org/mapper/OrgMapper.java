package com.xfs.org.mapper;

import com.xfs.org.pojo.dto.OrgQuery;
import com.xfs.org.pojo.entity.Org;
import com.xfs.org.pojo.vo.OrgVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgMapper {
    List<OrgVO> selectOrg(OrgQuery orgQuery);

    OrgVO selectById(Long id);

    void insert(Org org);

    void update(Org org);

    void deleteById(Long id);

    Integer countChildren(Long parentId);
}
