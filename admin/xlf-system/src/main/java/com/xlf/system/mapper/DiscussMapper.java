package com.xlf.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.xlf.system.domain.Discuss;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xlf.system.domain.Topic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 小新
* @description 针对表【discuss】的数据库操作Mapper
* @createDate 2023-02-23 21:52:17
* @Entity com.xlf.system.domain.Discuss
*/
public interface DiscussMapper extends BaseMapper<Discuss> {

    List<Discuss> getDisCussListByTopicId(@Param(Constants.WRAPPER)  QueryWrapper<Discuss> wrapper);
}




