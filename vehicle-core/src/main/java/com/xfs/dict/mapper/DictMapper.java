package com.xfs.dict.mapper;

import com.xfs.dict.pojo.dto.DictQuery;
import com.xfs.dict.pojo.entity.Dict;
import com.xfs.dict.pojo.vo.DictVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictMapper {
    List<DictVO> selectDict(DictQuery dictQuery);

    void insert(Dict dict);

    void update(Dict dict);
}
