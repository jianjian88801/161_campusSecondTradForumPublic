package com.xlf.framework.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.Fans;
import com.xlf.system.domain.Follow;
import com.xlf.system.mapper.FansMapper;
import com.xlf.system.mapper.FollowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

import static com.xlf.common.constant.Constants.USER_FANS;
import static com.xlf.common.constant.Constants.USER_FOLLOW;
import static com.xlf.common.constant.TalkConstants.USER_TALK_COLLECTION;

/**
 * 更新用户的粉丝列表
 */
@Service
@Slf4j
public class UpdateUserFansTask implements Runnable {

    @Resource
    FansMapper fansMapper;


    @Resource
    RedisCache redisCache;

    @Transactional
    @Override
    public void run() {

        Set<String> keys = redisCache.keys(USER_FANS);
        log.info("更新用户粉丝列表开始");
        if (StringUtils.isNotEmpty(keys)) {
            for (String item : keys) {

                Set<ZSetOperations.TypedTuple<String>> cacheZSetWithScore = redisCache.getCacheZSetWithScore(item);
                if(StringUtils.isNotEmpty(cacheZSetWithScore)){
                    String[] split = item.split(":");
                    String userId = split[split.length-1];
                    fansMapper.delete(new LambdaQueryWrapper<Fans>().eq(Fans::getUserId,userId));

                    cacheZSetWithScore.forEach(item2->{
                        Fans fans = new Fans();
                        fans.setUserId(Long.parseLong(userId));
                        fans.setFanId(Long.parseLong(item2.getValue()));
                        fans.setCreateTime(new Date(item2.getScore().longValue()));
                        fansMapper.insert(fans);
                    });
                }
            }
        }
        log.info("更新用户粉丝列表结束");
    }
}
