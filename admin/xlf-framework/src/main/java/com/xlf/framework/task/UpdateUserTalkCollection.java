package com.xlf.framework.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.Talk;
import com.xlf.system.domain.TalkThumbs;
import com.xlf.system.domain.UserCollectionTalk;
import com.xlf.system.service.TalkService;
import com.xlf.system.service.UserCollectionTalkService;
import com.xlf.system.service.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xlf.common.constant.TalkConstants.USER_TALK_COLLECTION;
import static com.xlf.common.constant.TopicConstants.USER_TOPIC_COLLECTION;

/**
 * 同步用户收藏talk 数据
 */
@Service
@Slf4j
public class UpdateUserTalkCollection implements Runnable{



    @Resource
    RedisCache redisCache;

    @Resource
    UserCollectionTalkService userCollectionTalkService;

    @Resource
    TalkService talkService;


    private static char SPLIT = ',';

    @Transactional
    @Override
    public void run() {

        //所有点赞过的topic
        Set<String> keys = redisCache.keys(USER_TALK_COLLECTION);
        log.info("talk用户收藏同步数据库开始");
        if(StringUtils.isNotEmpty(keys)){
            for (String key : keys) {//

                String[] split = key.split(":");
                Long userId = Long.parseLong(split[split.length-1]);

                Set<ZSetOperations.TypedTuple<String>> cacheZSetWithScore = redisCache.getCacheZSetWithScore(key);

                userCollectionTalkService.remove(new LambdaQueryWrapper<UserCollectionTalk>().eq(UserCollectionTalk::getUserId, userId));
                List<UserCollectionTalk> collect = cacheZSetWithScore.stream().map(item -> UserCollectionTalk
                                .builder()
                                .talkId(Long.parseLong(item.getValue()))
                                .userId(userId)
                                .createTime(new Date(item.getScore().longValue()))
                                .build())
                        .collect(Collectors.toList());

                //剔除被删除的帖子
                collect.forEach(item->{
                    Talk one = talkService.getOne(new LambdaQueryWrapper<Talk>().eq(Talk::getId,item.getTalkId()).select(Talk::getDelFlag));
                    if(one!=null&&one.getDelFlag()==0){
                        userCollectionTalkService.save(item);
                    }
                });
                //设置空值
                UserCollectionTalk NULL = UserCollectionTalk.builder()
                        .talkId(-1L)
                        .userId(userId)
                        .createTime(new Date(-1))
                        .build();
                userCollectionTalkService.save(NULL);

                //删除缓存
                redisCache.deleteObject(key);
                log.info("用户:{}的收藏同步数据库结束",userId);

            }
        }


    }
}
