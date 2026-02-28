package com.xfs.dictoption.service;

import com.xfs.dictoption.pojo.dto.DictOptionQuery;
import com.xfs.dictoption.pojo.dto.DictOptionSaveParam;
import com.xfs.dictoption.pojo.vo.DictOptionVO;

import java.util.List;

public interface DictOptionService {
    List<DictOptionVO> selectDictOption(DictOptionQuery dictOptionQuery);

    void saveDictOption(DictOptionSaveParam dictOptionSaveParam);

    void deleteDictOption(Long id);

    List<DictOptionVO> selectDictOptionByCode(String code);
}
