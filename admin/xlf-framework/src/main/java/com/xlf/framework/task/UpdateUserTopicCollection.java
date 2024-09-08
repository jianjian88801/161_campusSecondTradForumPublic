package com.xlf.framework.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.*;
import com.xlf.system.service.ThumbsService;
import com.xlf.system.service.TopicService;
import com.xlf.system.service.UserCollectionTopicService;
import com.xlf.system.service.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xlf.common.constant.TopicConstants.TOPIC_THUMBS;
import static com.xlf.common.constant.TopicConstants.USER_TOPIC_COLLECTION;

/**
 * 同步用户收藏数据
 */
@Service
@Slf4j
public class UpdateUserTopicCollection implements Runnable{

    @Resource
    RedisCache redisCache;

    @Resource
    UserCollectionTopicService userCollectionTopicService;

    @Resource
    TopicService topicService;


    private static char SPLIT = ',';

    @Override
    public void run() {

        //所有点赞过的topic
        Set<String> keys = redisCache.keys(USER_TOPIC_COLLECTION);
        log.info("用户收藏topic同步数据库开始");
        if(StringUtils.isNotEmpty(keys)){
            for (String key : keys) {//
                String[] split = key.split(":");
                Long userId = Long.parseLong(split[split.length-1]);

                Set<ZSetOperations.TypedTuple<String>> cacheZSetWithScore = redisCache.getCacheZSetWithScore(key);

                userCollectionTopicService.remove(new LambdaQueryWrapper<UserCollectionTopic>().eq(UserCollectionTopic::getUserId, userId));
                List<UserCollectionTopic> collect = cacheZSetWithScore.stream().map(item -> UserCollectionTopic
                                .builder()
                                .topicId(Long.parseLong(item.getValue()))
                                .userId(userId)
                                .createTime(new Date(item.getScore().longValue()))
                                .build())
                        .collect(Collectors.toList());

                //剔除被删除的
                collect.forEach(item->{
                    Topic one = topicService.getOne(new LambdaQueryWrapper<Topic>().eq(Topic::getId,item.getTopicId()).select(Topic::getDelFlag));
                    if(one!=null&&one.getDelFlag()==0){
                        userCollectionTopicService.save(item);
                    }
                });
                //设置空值
                UserCollectionTopic NULL = UserCollectionTopic.builder()
                        .topicId(-1L)
                        .userId(userId)
                        .createTime(new Date(-1))
                        .build();
                userCollectionTopicService.save(NULL);

                //删除缓存
                redisCache.deleteObject(key);
                log.info("用户:{}的topic收藏同步数据库结束",userId);

            }
        }


    }
}
