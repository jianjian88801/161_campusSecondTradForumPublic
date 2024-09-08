package com.xlf.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.xlf.system.domain.Topic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 小新
* @description 针对表【topic】的数据库操作Mapper
* @createDate 2023-02-23 22:07:00
* @Entity com.xlf.system.domain.Topic
*/
public interface TopicMapper extends BaseMapper<Topic> {

    /**
     * 拿topic列表
     * @return
     */
    List<Topic> getListByWrapper(@Param(Constants.WRAPPER) Wrapper wrapper);

    /**
     * 通过id拿topic
     * @param wrapper
     * @return
     */
    Topic getTopicById(@Param(Constants.WRAPPER)  QueryWrapper<Topic> wrapper);
}




