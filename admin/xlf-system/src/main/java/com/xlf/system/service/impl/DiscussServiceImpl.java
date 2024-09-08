package com.xlf.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.security.util.SecurityUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.common.util.StringUtils;
import com.xlf.system.domain.Comment;
import com.xlf.system.domain.Discuss;
import com.xlf.system.domain.DiscussPicture;
import com.xlf.system.mapper.CommentMapper;
import com.xlf.system.mapper.DiscussPictureMapper;
import com.xlf.system.mapper.SysUserMapper;
import com.xlf.system.service.DiscussService;
import com.xlf.system.mapper.DiscussMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.xlf.common.constant.DiscussConstants.DISCUSS_THUMBS;
import static com.xlf.common.constant.TopicConstants.TOPIC_DISCUSS_NUM;
import static com.xlf.common.constant.TopicConstants.getTopicDiscussNumKey;

/**
* @author 小新
* @description 针对表【discuss】的数据库操作Service实现
* @createDate 2023-02-23 21:52:17
*/
@Service
public class DiscussServiceImpl extends ServiceImpl<DiscussMapper, Discuss>
    implements DiscussService{

    @Resource
    DiscussMapper discussMapper;

    @Resource
    CommentMapper commentMapper;

    @Resource
    SysUserMapper userMapper;

    @Resource
    DiscussPictureMapper discussPictureMapper;

    @Resource
    RedisCache redisCache;

    /**
     * discuss
     * @param topicId
     * @return
     */
    @Override
    public List<Discuss> getDisCussListByTopicId(Integer orderType,Long topicId) {

        QueryWrapper<Discuss> wrapper = new QueryWrapper<>();
        wrapper.eq("d.topic_id",topicId);
        wrapper.eq("d.del_flag",0);
        wrapper.groupBy("d.id");

        //根据发布时间排
        if(orderType==1){
            wrapper.orderByAsc("d.create_time");
            //根据最热排
        }else if(orderType==2){
            wrapper.orderByAsc("thumbsNum");
        }else {

        }
        //查出评论list
        List<Discuss> list = discussMapper.getDisCussListByTopicId(wrapper);
        if(StringUtils.isEmpty(list))
            return new ArrayList<>();

        //所有ids
        List<Long> discussIds = list.stream().map(item -> item.getId()).collect(Collectors.toList());

        //查出话题评论的人ids
        List<Long> userIds = list.stream().map(item -> item.getUserId()).collect(Collectors.toList());

        //sql:根据ids查出所有评论
        List<Comment> comments = commentMapper.selectList(new LambdaQueryWrapper<Comment>().in(Comment::getDiscussId, discussIds));

        //sql:根据ids查出所有图片
        List<DiscussPicture> discussPictures = discussPictureMapper.selectList(new LambdaQueryWrapper<DiscussPicture>().in(DiscussPicture::getDiscussId,discussIds));

        //获取话题评论的评论的用户ids
        Optional.ofNullable(comments).ifPresent(comments1 -> {
           //获取评论的人ids
            userIds.addAll(comments1.stream().map(item->item.getUserId()).collect(Collectors.toList()));
            //获取被评论的人ids
            userIds.addAll(comments1.stream().map(item->item.getParentId()).collect(Collectors.toList()));
        });
        userIds.removeIf(item->item==null);
        Set<Long> collect = userIds.stream().collect(Collectors.toSet());
        //sql:查出所有用户
        List<SysUser> userList = userMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, collect));
        Map<Long, SysUser> userHashMap = userList.stream().collect(Collectors.toMap(SysUser::getUserId, t -> t));


        //封装评论
        Optional.ofNullable(comments).ifPresent(comments1 -> {
            comments1.forEach(item->{
                item.setUser(userHashMap.get(item.getUserId()));
                if(item.getParentId()!=null)
                    item.setUserBy(userHashMap.get(item.getParentId()));
            });
            list.forEach(item->{
                item.setComment(comments1.stream()
                        .filter(comment -> comment.getDiscussId().equals(item.getId()))
                        .collect(Collectors.toList()));
            });
        });
        //封装用户
        list.forEach(item->{
            item.setUser(userHashMap.get(item.getUserId()));
            item.setThumbs(0);
        });
        //封装图片
        Optional.ofNullable(discussPictures).ifPresent(discussPictures1 -> {
            list.forEach(item-> {
                item.setPictrueList(discussPictures1.stream()
                        .filter(discussPicture -> discussPicture.getDiscussId().equals(item.getId()))
                        .map(discussPicture -> discussPicture.getUrl())
                        .collect(Collectors.toList()));
            });
        });

        //获取点赞信息
        try {
            Long userId = (Long) SecurityUtils.getUserId();
            list.forEach(item->{
                String key =  DISCUSS_THUMBS+item.getId();
                item.setThumbsNum(Integer.parseInt(redisCache.getSetSize(key).toString()));
                if(redisCache.isSetMember(key,userId))
                    item.setThumbs(1);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    @Transactional
    @Override
    public void addDiscuss(Discuss discuss,Long userId) {

        discuss.setUserId(userId);
        save(discuss);
        String key = getTopicDiscussNumKey(discuss.getTopicId().toString());
        redisCache.increment(key);
        Optional.ofNullable(discuss.getPictrueList()).ifPresent(item->{
            item.forEach(pictrue->{
                DiscussPicture discussPicture = new DiscussPicture();
                discussPicture.setDiscussId(discuss.getId());
                discussPicture.setUrl(pictrue);
                discussPictureMapper.insert(discussPicture);
            });
        });
    }
}




