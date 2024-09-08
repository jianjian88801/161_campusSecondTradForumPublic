package com.xlf.system.service;

import com.xlf.system.domain.Discuss;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 小新
* @description 针对表【discuss】的数据库操作Service
* @createDate 2023-02-23 21:52:17
*/
public interface DiscussService extends IService<Discuss> {

    /**
     * 根据topicId拿评论列表
     * @param topicId
     * @return
     */
    List<Discuss> getDisCussListByTopicId(Integer orderType,Long topicId);

    void addDiscuss(Discuss discuss,Long userId);
}
