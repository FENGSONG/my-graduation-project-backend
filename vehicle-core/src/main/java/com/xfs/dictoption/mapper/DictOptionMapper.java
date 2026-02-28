package com.xfs.dictoption.mapper;

import com.xfs.dictoption.pojo.dto.DictOptionQuery;
import com.xfs.dictoption.pojo.entity.DictOption;
import com.xfs.dictoption.pojo.vo.DictOptionVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictOptionMapper {
    List<DictOptionVO> selectDictOption(DictOptionQuery dictOptionQuery);

    void insert(DictOption dictOption);

    void update(DictOption dictOption);

    void deleteById(Long id);
}
