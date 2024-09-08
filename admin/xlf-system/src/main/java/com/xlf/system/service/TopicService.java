package com.xlf.system.service;

import com.xlf.system.domain.Topic;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 小新
* @description 针对表【topic】的数据库操作Service
* @createDate 2023-02-23 21:51:44
*/
public interface TopicService extends IService<Topic> {


    List<Topic> getPage(Integer orderType, String title);


    /**
     * 通过id拿topic
     * @param id
     * @return
     */
    Topic getTopicById(Long id);

    /**
     * 新增，修改
     * @param topic

     */
    void saveOrUpdateTopic(Topic topic);

    void deleteById(Long topicId);

    void collectionTopic(Long topicId);

    void thumbs(Long topicId);
}
