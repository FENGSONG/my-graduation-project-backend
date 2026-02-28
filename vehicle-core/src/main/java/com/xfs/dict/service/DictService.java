package com.xfs.dict.service;

import com.xfs.dict.pojo.dto.DictQuery;
import com.xfs.dict.pojo.dto.DictSaveParam;
import com.xfs.dict.pojo.vo.DictVO;

import java.util.List;

public interface DictService {
    List<DictVO> selectDict(DictQuery dictQuery);

    void saveDict(DictSaveParam dictSaveParam);

    void deleteDict(Long id);
}
