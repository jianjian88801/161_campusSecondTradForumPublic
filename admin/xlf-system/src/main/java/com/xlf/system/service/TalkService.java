package com.xlf.system.service;

import com.xlf.common.util.page.PageResult;
import com.xlf.common.util.page.TableDataInfo;
import com.xlf.system.domain.Talk;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 小新
* @description 针对表【talk】的数据库操作Service
* @createDate 2023-04-11 19:30:20
*/
public interface TalkService extends IService<Talk> {

    List<Talk> getPage(Long talkId,Long typeId, Long userId,String content);


    List<Talk> getPage(Talk talk);

    PageResult getFollowTalk(Long time, Integer offSet, Integer pageSize);

    Talk getTalkById(String talkId);

    void addTalk(Talk talk, Long userId);

    void thumbs(Long talkId);

    void collectionTalk(Long talkId);

    void updateTalk(Talk talk);

    void deleteTalks(List<Long> asList);

    Talk getTalk(Long talkId);

    PageResult getCollectionTalk(Long time, Integer offSet, Integer pageSize);
}
