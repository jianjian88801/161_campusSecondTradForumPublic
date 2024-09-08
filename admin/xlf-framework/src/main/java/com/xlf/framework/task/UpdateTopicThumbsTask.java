package com.xlf.framework.task;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.TalkThumbs;
import com.xlf.system.domain.Thumbs;
import com.xlf.system.service.ThumbsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xlf.common.constant.TopicConstants.TOPIC_THUMBS;

/**
 * 将点赞同步到数据库
 */
@Service
@Slf4j
public class UpdateTopicThumbsTask implements Runnable{

    @Resource
    RedisCache redisCache;

    @Resource
    ThumbsService thumbsService;


    private static char SPLIT = ',';

    @Override
    public void run() {


        //所有点赞过的topic
        Set<String> keys = redisCache.keys(TOPIC_THUMBS);
        log.info("topic点赞同步数据库开始");
        if(StringUtils.isNotEmpty(keys)){
            for (String key : keys) {//
                String[] split = key.split(":");
                Long topicId = Long.parseLong(split[split.length-1]);

                Set<ZSetOperations.TypedTuple<String>> cacheZSetWithScore = redisCache.getCacheZSetWithScore(key);

                thumbsService.remove(new LambdaQueryWrapper<Thumbs>().eq(Thumbs::getTopicId, topicId));
                List<Thumbs> collect = cacheZSetWithScore.stream().map(item -> Thumbs
                                .builder()
                                .topicId(topicId)
                                .userId(Long.parseLong(item.getValue()))
                                .createTime(new Date(item.getScore().longValue()))
                                .build())
                        .collect(Collectors.toList());

                thumbsService.saveBatch(collect);

                //删除缓存
                redisCache.deleteObject(key);
                log.info("同步topic:{}的点赞列表",topicId);
            }
        }


    }
}
