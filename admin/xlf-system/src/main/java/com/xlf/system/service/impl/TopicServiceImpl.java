package com.xlf.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.common.security.util.SecurityUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.common.util.StringUtils;
import com.xlf.system.domain.*;
import com.xlf.system.mapper.DiscussMapper;
import com.xlf.system.mapper.ThumbsMapper;
import com.xlf.system.mapper.UserDetailsMapper;
import com.xlf.system.service.RedisCacheBuilder;
import com.xlf.system.service.TopicService;
import com.xlf.system.mapper.TopicMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xlf.common.constant.TalkConstants.getTalkThumbs;
import static com.xlf.common.constant.TalkConstants.getUserTalkCollection;
import static com.xlf.common.constant.TopicConstants.*;

/**
* @author 小新
* @description 针对表【topic】的数据库操作Service实现
* @createDate 2023-02-23 21:51:44
*/
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic>
    implements TopicService{


    @Resource
    TopicMapper topicMapper;

    @Resource
    RedisCache redisCache;

    @Resource
    RedisCacheBuilder redisCacheBuilder;
    @Resource
    DiscussMapper discussMapper;

   @Override
    public List<Topic> getPage(Integer orderType, String title)
    {

        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<Topic>();
        wrapper.like(StringUtils.isNotEmpty(title),Topic::getTitle,title);
        wrapper.eq(Topic::getDelFlag,0);

        //根据发布时间排
        if(orderType==1){
            wrapper.orderByDesc(Topic::getCreateTime);
            //根据最热排
        }else if(orderType==2){
            wrapper.orderByDesc(Topic::getView);
        }else {
            //获取收藏列表
            Set<String> topicIds = getStarCollection();
            if(StringUtils.isNotEmpty(topicIds)&&topicIds.size()>1)
                wrapper.in(Topic::getId,topicIds);
            else
                return new ArrayList<>();
        }

        List<Topic> list =  list(wrapper);

        if(list!=null){
            list.forEach(item->{
                //访问量
                setTopicView(item);
                //点赞量
                setTopicThumbNum(item);
                //评论量
                setTopicDiscussNum(item);
             });
        }

        return list;

    }
    /**
     * 通过id拿topic
     * @param id
     * @return
     */
    @Override
    public Topic getTopicById(Long id) {
        QueryWrapper<Topic> wrapper = new QueryWrapper<Topic>();
        wrapper.eq("id",id);
        wrapper.eq("del_flag",0);
        Topic topic = getOne(wrapper);

        Optional.ofNullable(topic).ifPresent(item->{
            //浏览量+1
            setTopicView(topic,1L);
            //设置点赞量
            setTopicThumbNum(topic);
            //设置评论量
            setTopicDiscussNum(topic);
            //判断是否点赞和收藏信息
            isThumbAndCollection(item);
        });

        return topic;
    }

    /**
     * 判断用户是否收藏，没有就重建
     * @param item
     */
    private void isThumbAndCollection(Topic item) {

        item.setThumbs(0);
        item.setCollection(0);
        //如果用户已登录
        try {

            String userId =((Long) SecurityUtils.getUserId()).toString();

            //判断用户是否点赞。这里保证数据库数据已经同步到redis
            if(redisCache.isZSetMember(getTopicThumbsSetKey(item.getId().toString()),userId)){
                item.setThumbs(1);
            }

            //判断用户是否收藏
            String key = getUserTopicCollection(userId);
            redisCache.consumerWithMutexAndBuild(key,(k)->{
                item.setCollection(redisCache.isZSetMember(k,item.getId().toString())?1:0);
            },(k)->redisCacheBuilder.buildTopicCollectionCache((Long) SecurityUtils.getUserId(),k));

        } catch (Exception e) {
            //不处理
        }
    }

    /**
     * 设置点赞量.
     * 先从缓存中拿，如果缓存没有就重建缓存
     * @param topic
     */
    private void setTopicThumbNum(Topic topic) {

        String key = getTopicThumbsSetKey(topic.getId().toString());

        Long thumbsNum = redisCache.queryWithMutexAndBuild(key, (s) -> redisCache.getZSetSize(s), (s) -> {
            redisCacheBuilder.buildTopicThumbCache(topic.getId(), s);
            return redisCache.getZSetSize(s);
        });
        topic.setThumbsNum(thumbsNum.intValue()-1);
    }

    /**
     * 设置浏览量，如果缓存没有就重建
     * @param topic
     */
    private void setTopicView(Topic topic,Long isIncrement) {

        Long increment = redisCache.mapIncrement(TOPIC_VIEW, topic.getId().toString(), isIncrement);

        //无缓存
        if(topic.getView()>increment){
            topic.setView(topic.getView()+isIncrement.intValue());
            redisCache.setCacheMapValue(TOPIC_VIEW, topic.getId().toString(), topic.getView());
        }
        else
            topic.setView(increment.intValue());
    }
    /**
     * 设置浏览量
     * @param topic
     */
    private void setTopicView(Topic topic) {

       setTopicView(topic,0L);
    }

    /**
     * 设置topic的评论数量
     * @param topic
     */
    private void setTopicDiscussNum(Topic topic){

        String key = getTopicDiscussNumKey(topic.getId().toString());

        Integer discussNum = redisCache.queryWithMutexAndBuild(key, (s) -> redisCache.getCacheObject(s), (s) -> {
            //重建缓存
            Integer num = discussMapper.selectCount(new LambdaQueryWrapper<Discuss>().eq(Discuss::getTopicId, topic.getId())).intValue();
            if(num==null)
                num = 0;
            redisCache.setCacheObject(s,num);
            return num;
        });

        topic.setDiscussNum(discussNum);
    }

    @Transactional
    @Override
    public void saveOrUpdateTopic(Topic topic) {
        topic.setView(0);
        save(topic);

    }

    @Transactional
    @Override
    public void deleteById(Long topicId) {
        // 1.
        Topic topic = new Topic();
        topic.setId(topicId);
        topic.setDelFlag(1);
        topicMapper.updateById(topic);
    }

    /**
     * 点赞
     * @param topicId
     */
    @Override
    public void thumbs(Long topicId) {

        String userId = ((Long)SecurityUtils.getUserId()).toString();
        String key =  getTopicThumbsSetKey(topicId.toString());

        redisCache.consumerWithMutexAndBuild(key,(k)->{
            if(redisCache.isZSetMember(k,userId))
                redisCache.removeZSet(k,userId);
            else
                redisCache.setCacheZSet(k,userId,System.currentTimeMillis());
        },(k)->redisCacheBuilder.buildTopicThumbCache(topicId,k));
    }

    /**
     * 收藏
     * @param topicId
     */
    @Override
    public void collectionTopic(Long topicId) {
        if(topicId==-1L)
            return;
        Long userId = ((Long) SecurityUtils.getUserId());
        String key =  getUserTopicCollection(userId.toString());
        redisCache.consumerWithMutexAndBuild(key, (k) -> {
            if(redisCache.isZSetMember(k,topicId.toString()))
                redisCache.removeZSet(k,topicId.toString());
            else
                redisCache.setCacheZSet(k,topicId.toString(),System.currentTimeMillis());
        }, (k)-> redisCacheBuilder.buildTopicCollectionCache(userId,k));
    }


    /**
     * 获取用户收藏topic列表
     * @return
     */
    private Set<String> getStarCollection() {
        Long userId = (Long) SecurityUtils.getUserId();
        String key =  getUserTopicCollection(userId.toString());
        Set<String> topicIds = redisCache.queryWithMutexAndBuild(key, (k) -> redisCache.getCacheZSet(k), (k) -> {
            redisCacheBuilder.buildTopicCollectionCache(userId, k);
            return redisCache.getCacheZSet(k);
        });
        return topicIds;
    }


}





