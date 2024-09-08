package com.xlf.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xlf.system.domain.Sell;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 小新
* @description 针对表【sell】的数据库操作Mapper
* @createDate 2023-03-15 14:47:06
* @Entity com.xlf.system.domain.Sell
*/
public interface SellMapper extends BaseMapper<Sell> {

    List<Sell> getListByWrapper(QueryWrapper<Sell> wrapper);
}




