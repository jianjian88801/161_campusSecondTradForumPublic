package com.xlf.system.service;

import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.system.domain.Talk;
import com.xlf.system.domain.TalkComment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 小新
* @description 针对表【talk_comment】的数据库操作Service
* @createDate 2023-04-11 19:36:13
*/
public interface TalkCommentService extends IService<TalkComment> {

    List<TalkComment> getList(Long talkId,Integer orderType);

    TalkComment saveTalkComment(TalkComment talkComment);

    List<TalkComment> getTalkCommentPage(TalkComment talkComment);

    void deleteTalkComments(List<Long> asList);
}
