package com.xfs.dictoption.service.impl;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import com.xfs.dict.mapper.DictMapper;
import com.xfs.dict.pojo.dto.DictQuery;
import com.xfs.dict.pojo.vo.DictVO;
import com.xfs.dictoption.mapper.DictOptionMapper;
import com.xfs.dictoption.pojo.dto.DictOptionQuery;
import com.xfs.dictoption.pojo.dto.DictOptionSaveParam;
import com.xfs.dictoption.pojo.entity.DictOption;
import com.xfs.dictoption.pojo.vo.DictOptionVO;
import com.xfs.dictoption.service.DictOptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class DictOptionServiceImpl implements DictOptionService {
    @Autowired
    DictOptionMapper dictOptionMapper;
    @Autowired
    DictMapper dictMapper;

    @Override
    public List<DictOptionVO> selectDictOption(DictOptionQuery dictOptionQuery) {
        log.debug("查询字典项业务参数：{}", dictOptionQuery);
        List<DictOptionVO> list = dictOptionMapper.selectDictOption(dictOptionQuery);
        return list;
    }

    @Override
    public void saveDictOption(DictOptionSaveParam dictOptionSaveParam) {
        log.debug("保存字典项业务参数：{}", dictOptionSaveParam);
        DictOption dictOption = new DictOption();
        BeanUtils.copyProperties(dictOptionSaveParam, dictOption);
        if(dictOption.getId() == null){
            dictOption.setCreateTime(new Date());
            dictOptionMapper.insert(dictOption);
        }else{
            dictOption.setUpdateTime(new Date());
            dictOptionMapper.update(dictOption);
        }
    }

    @Override
    public void deleteDictOption(Long id) {
        log.debug("删除字典项业务参数：{}", id);
        dictOptionMapper.deleteById(id);
    }

    @Override
    public List<DictOptionVO> selectDictOptionByCode(String code) {
        log.debug("根据字典编码查询字典项业务，code={}",code);
        //需要先根据字典编码,查询字典是否存在,若存在,再查询该字典下的字典项
        DictQuery dictQuery = new DictQuery();//封装字典查询对象
        dictQuery.setCode(code);//给字典查询对象设置字典编码
        List<DictVO> dictVOList = dictMapper.selectDict(dictQuery);//查询字典
        if(dictVOList!=null && dictVOList.size()>0){//如果根据code查到字典存在
            DictVO dictVO = dictVOList.get(0);//获取当前code对应的那一个字典
            DictOptionQuery dictOptionQuery = new DictOptionQuery();//封装字典项查询对象
            dictOptionQuery.setDictId(dictVO.getId());//给字典项查询对象设置字典id
            //查询字典项
            List<DictOptionVO> dictOptionVOList = dictOptionMapper.selectDictOption(dictOptionQuery);
            return dictOptionVOList;//返回查到的该字典code对应的所有字典项
        }else{
            //如果根据code查不到字典数据,抛出字典数据不存在的异常
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
    }
}
