package com.xlf.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.security.util.SecurityUtils;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.Talk;
import com.xlf.system.domain.TalkComment;
import com.xlf.system.mapper.SysUserMapper;
import com.xlf.system.mapper.TalkMapper;
import com.xlf.system.mapper.UserDetailsMapper;
import com.xlf.system.mq.builder.SubscribeService;
import com.xlf.system.mq.producer.TalkProducer;
import com.xlf.system.service.TalkCommentService;
import com.xlf.system.mapper.TalkCommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.xlf.common.constant.RedisConst.TALK_COMMENT_SUBSCRIPTION;
import static com.xlf.common.util.date.TimeAgoUtils.format;

/**
* @author 小新
* @description 针对表【talk_comment】的数据库操作Service实现
* @createDate 2023-04-11 19:36:13
*/
@Service
public class TalkCommentServiceImpl extends ServiceImpl<TalkCommentMapper, TalkComment>
    implements TalkCommentService{

    @Resource
    TalkMapper talkMapper;

    @Resource
    SysUserMapper sysUserMapper;


    @Resource
    TalkCommentMapper talkCommentMapper;

    @Resource
    TalkProducer talkProducer;




    @Override
    public List<TalkComment> getList(Long talkId, Integer orderType) {

        LambdaQueryWrapper<TalkComment> talkCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        talkCommentLambdaQueryWrapper.eq(talkId!=null&&!talkId.equals(-1L),TalkComment::getTalkId,talkId);
        talkCommentLambdaQueryWrapper.eq(TalkComment::getDelFlag,0);
        talkCommentLambdaQueryWrapper.orderByDesc(TalkComment::getCreateTime);
        //回复我的
        List<TalkComment> list;
        if(orderType==2)
        {
            //根据回复我
             list = talkCommentMapper.getTalkCommentByUserById((Long)SecurityUtils.getUserId());
        }
        else
            list = list(talkCommentLambdaQueryWrapper);

        if(StringUtils.isNotEmpty(list)){
            list.forEach(item->{
                //设置时间
                item.setDate(format(item.getCreateTime()));
                item.setUser(sysUserMapper.getNickNameAndAvatarByUserIdSysUser(item.getUserId()));
                if(item.getParentId()!=null){
                    TalkComment talkComment = talkCommentMapper.selectById(item.getParentId());
                    if(talkComment==null||talkComment.getDelFlag()==1){
                        item.setCommentBy("该评论已被删除");
                    }else{
                        item.setCommentBy(talkComment.getContent());
                        item.setUserBy(sysUserMapper.getNickNameAndAvatarByUserIdSysUser(talkComment.getUserId()));
                    }

                }
            });
        }

        return list;
    }

    /**
     * 评论
     * @param item
     */
    @Override
    public TalkComment saveTalkComment(TalkComment item) {

        save(item);
        //设置时间
        item.setDate(format(new Date()));
        //回复的人
        item.setUser(sysUserMapper.getNickNameAndAvatarByUserIdSysUser(item.getUserId()));

        //被回复的人
        if(item.getParentId()!=null){
            TalkComment talkComment = talkCommentMapper.selectById(item.getParentId());
            if(talkComment==null||talkComment.getDelFlag()==1){
                item.setCommentBy("该评论已被删除");
            }else{
                item.setCommentBy(talkComment.getContent());
                item.setUserBy(sysUserMapper.getNickNameAndAvatarByUserIdSysUser(talkComment.getUserId()));
            }

        }
        //消息推送
        talkProducer.sendMsg(item);
        return item;
    }

    @Override
    public List<TalkComment> getTalkCommentPage(TalkComment talkComment) {

        LambdaQueryWrapper<TalkComment> wrapper = new LambdaQueryWrapper<TalkComment>();

        wrapper.eq(talkComment.getTalkId()!=null,TalkComment::getTalkId,talkComment.getTalkId());

        wrapper.eq(talkComment.getDelFlag()!=null,TalkComment::getDelFlag,talkComment.getDelFlag());

        wrapper.like(StringUtils.isNotEmpty(talkComment.getContent()),TalkComment::getContent,talkComment.getContent());

        if(talkComment.getParams().containsKey("beginTime")&&talkComment.getParams().containsKey("endTime"))
            wrapper.between(TalkComment::getCreateTime,talkComment.getParams().get("beginTime"),talkComment.getParams().get("endTime"));

        wrapper.eq(talkComment.getUserId()!=null,TalkComment::getUserId,talkComment.getUserId());

        wrapper.orderByDesc(TalkComment::getCreateTime);

        List<TalkComment> list = list(wrapper);
        if(StringUtils.isNotEmpty(list)){
            list.forEach(item->{
                item.setUser(sysUserMapper.getNickNameAndAvatarByUserIdSysUser(item.getUserId()));
                if(item.getParentId()!=null){
                    TalkComment children = talkCommentMapper.selectById(item.getParentId());
                    if(children==null||children.getDelFlag()==1){
                        item.setCommentBy("该评论已被删除");
                    }else{
                        item.setCommentBy(children.getContent());
                        item.setUserBy(sysUserMapper.getNickNameAndAvatarByUserIdSysUser(children.getUserId()));
                    }

                }
            });
        }
        return list;
    }

    /**
     * 删除
     * @param asList
     */
    @Transactional
    @Override
    public void deleteTalkComments(List<Long> asList) {
        removeBatchByIds(asList);
    }
}




