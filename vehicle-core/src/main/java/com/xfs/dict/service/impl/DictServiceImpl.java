package com.xfs.dict.service.impl;

import com.xfs.dict.mapper.DictMapper;
import com.xfs.dict.pojo.dto.DictQuery;
import com.xfs.dict.pojo.dto.DictSaveParam;
import com.xfs.dict.pojo.entity.Dict;
import com.xfs.dict.pojo.vo.DictVO;
import com.xfs.dict.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class DictServiceImpl implements DictService {
    @Autowired
    DictMapper dictMapper;

    @Override
    public List<DictVO> selectDict(DictQuery dictQuery) {
        log.debug("查询字典业务，参数：{}",dictQuery);
        List<DictVO> list = dictMapper.selectDict(dictQuery);
        return list;
    }

    @Override
    public void saveDict(DictSaveParam dictSaveParam) {
        log.debug("保存字典业务，参数：{}",dictSaveParam);
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictSaveParam, dict);
        if(dict.getId() == null){
            dict.setStatus("1");//设置新增的字典默认都是启用状态
            dict.setCreateTime(new Date());
            dictMapper.insert(dict);
        }else{
            dict.setUpdateTime(new Date());
            dictMapper.update(dict);
        }
    }

    @Override
    public void deleteDict(Long id) {
        log.debug("删除字典业务，参数：{}",id);
        Dict dict = new Dict();
        dict.setId(id);
        dict.setStatus("0");//设置删除的字典状态为禁用
        dict.setUpdateTime(new Date());
        dictMapper.update(dict);
    }
}
