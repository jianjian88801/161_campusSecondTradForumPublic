package com.xlf.framework.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.Follow;
import com.xlf.system.mapper.FollowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

import static com.xlf.common.constant.Constants.USER_FOLLOW;
import static com.xlf.common.constant.TalkConstants.USER_TALK_COLLECTION;

/**
 * 更新用户的关注列表
 */
@Service
@Slf4j
public class UpdateUserFollowTask implements Runnable {

    @Resource
    FollowMapper followMapper;


    @Resource
    RedisCache redisCache;

    @Transactional
    @Override
    public void run() {

        Set<String> keys = redisCache.keys(USER_FOLLOW);
        log.info("更新用户关注列表开始");
        if (StringUtils.isNotEmpty(keys)) {
            for (String item : keys) {

                Set<ZSetOperations.TypedTuple<String>> cacheZSetWithScore = redisCache.getCacheZSetWithScore(item);
                if(StringUtils.isNotEmpty(cacheZSetWithScore)){
                    String[] split = item.split(":");
                    String userId = split[split.length-1];
                    followMapper.delete(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId,userId));

                    cacheZSetWithScore.forEach(item2->{
                        Follow follow = new Follow();
                        follow.setUserId(Long.parseLong(userId));
                        follow.setUserById(Long.parseLong(item2.getValue()));
                        follow.setCreateTime(new Date(item2.getScore().longValue()));
                        followMapper.insert(follow);
                    });
                }
            }
        }
        log.info("更新用户关注列表结束");
    }
}
