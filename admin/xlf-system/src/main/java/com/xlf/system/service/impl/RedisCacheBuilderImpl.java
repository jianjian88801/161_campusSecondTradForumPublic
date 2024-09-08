package com.xlf.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.common.util.spring.SpringUtils;
import com.xlf.system.domain.*;
import com.xlf.system.mapper.*;
import com.xlf.system.service.RedisCacheBuilder;
import com.xlf.system.service.TopicService;
import com.xlf.system.service.UserCollectionTalkService;
import com.xlf.system.service.UserCollectionTopicService;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.redis.core.ZSetOperations.TypedTuple.of;

@Service
public class RedisCacheBuilderImpl implements RedisCacheBuilder {


    @Resource
    FollowMapper followMapper;

    @Resource
    FansMapper fansMapper;

    @Resource
    RedisCache redisCache;

    @Resource
    UserCollectionTalkService userCollectionTalkService;

    @Resource
    TalkThumbsMapper talkThumbsMapper;

    @Resource
    ThumbsMapper thumbsMapper;

    @Resource
    UserCollectionTopicService userCollectionTopicService;

    //空值
    private String NULL = "-1";

    /**
     * 构建关注缓存
     * @param userId
     * @param key
     */
    @Override
    public void buildFollowCache(Long userId, String key) {

        Set<ZSetOperations.TypedTuple<String>> follows = followMapper.selectList(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUserId, userId)
                .select(Follow::getUserById,Follow::getCreateTime)).stream().map(item-> of(item.getUserById().toString(),(double)(item.getCreateTime().getTime()))).collect(Collectors.toSet());
        if(StringUtils.isEmpty(follows)){
            redisCache.setCacheZSet(key,NULL,-1);
        } else
          redisCache.setCacheZSet(key,follows);
    }


    /**
     * 构建粉丝缓存
     * @param userId
     * @param key
     */
    @Override
    public void buildFansCache(Long userId, String key) {

        Set<ZSetOperations.TypedTuple<String>> fans = fansMapper.selectList(new LambdaQueryWrapper<Fans>()
                .eq(Fans::getUserId, userId)
                .select(Fans::getFanId,Fans::getCreateTime)).stream().map(item-> of(item.getFanId().toString(),(double)(item.getCreateTime().getTime()))).collect(Collectors.toSet());

        if(StringUtils.isEmpty(fans)){
            redisCache.setCacheZSet(key,NULL,-1);
        } else
            redisCache.setCacheZSet(key,fans);
    }

    /**
     * 构建投喂箱缓存
     * @param userId
     * @param key
     */
    @Override
    public void buildUserDropBox(Long userId, String key) {

    }



    /**
     * 构建用户收藏talk缓存
     * @param userId
     * @param key
     */
    @Override
    public void buildTalkStarCollectionCache(Long userId, String key) {

        LambdaQueryWrapper<UserCollectionTalk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollectionTalk::getUserId,userId);
        wrapper.select(UserCollectionTalk::getTalkId,UserCollectionTalk::getCreateTime);
        List<UserCollectionTalk> list = userCollectionTalkService.list(wrapper);

        //空
        if(StringUtils.isEmpty(list)){
            redisCache.setCacheZSet(key,NULL,-1);
            return;
        }

        Set<ZSetOperations.TypedTuple<String>> collect = list.stream().map(item -> of(item.getTalkId().toString(), (double) (item.getCreateTime().getTime()))).collect(Collectors.toSet());
        redisCache.setCacheZSet(key,collect);


    }

    /**
     * 构建Talk点赞用户列表缓存
     * @param talkId
     * @param key
     */
    @Override
    public void buildTalkThumbCache(Long talkId, String key) {

        LambdaQueryWrapper<TalkThumbs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TalkThumbs::getTalkId,talkId);
        wrapper.select(TalkThumbs::getUserId,TalkThumbs::getCreateTime);
        List<TalkThumbs> list = talkThumbsMapper.selectList(wrapper);

        //空
        if(StringUtils.isEmpty(list)){
            redisCache.setCacheZSet(key,NULL,-1);
            return;
        }

        Set<ZSetOperations.TypedTuple<String>> collect = list.stream()
                .map(item -> of(item.getUserId().toString(), (double) (item.getCreateTime().getTime())))
                .collect(Collectors.toSet());
        redisCache.setCacheZSet(key,collect);
    }

    /**
     * 构建用户收藏topic列表缓存
     * @param userId
     * @param key
     * @return
     */
    public void buildTopicCollectionCache(Long userId,String key) {
        LambdaQueryWrapper<UserCollectionTopic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollectionTopic::getUserId,userId);
        wrapper.select(UserCollectionTopic::getTopicId,UserCollectionTopic::getCreateTime);
        List<UserCollectionTopic> list = userCollectionTopicService.list(wrapper);

        //空
        if(StringUtils.isEmpty(list)){
            redisCache.setCacheZSet(key,NULL,-1);
            return;
        }

        Set<ZSetOperations.TypedTuple<String>> collect = list.stream().map(item -> of(item.getTopicId().toString(), (double) (item.getCreateTime().getTime()))).collect(Collectors.toSet());
        redisCache.setCacheZSet(key,collect);
    }

    /**
     * 构建topic点赞用户列表缓存
     * @param topicId
     * @param key
     */
    public void buildTopicThumbCache(Long topicId, String key) {

        LambdaQueryWrapper<Thumbs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Thumbs::getTopicId,topicId);
        wrapper.select(Thumbs::getUserId,Thumbs::getCreateTime);
        List<Thumbs> list = thumbsMapper.selectList(wrapper);

        //空
        if(StringUtils.isEmpty(list)){
            redisCache.setCacheZSet(key,NULL,-1);
            return;
        }

        Set<ZSetOperations.TypedTuple<String>> collect = list.stream().map(item -> of(item.getUserId().toString(), (double) (item.getCreateTime().getTime()))).collect(Collectors.toSet());
        redisCache.setCacheZSet(key,collect);

    }

}
