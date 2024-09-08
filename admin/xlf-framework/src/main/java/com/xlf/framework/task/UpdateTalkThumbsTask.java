package com.xlf.framework.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.TalkThumbs;
import com.xlf.system.service.TalkThumbsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xlf.common.constant.TalkConstants.TALK_THUMBS;

@Service
@Slf4j
public class UpdateTalkThumbsTask implements Runnable{


    @Resource
    RedisCache redisCache;

    @Resource
    TalkThumbsService thumbsService;


    private static char SPLIT = ',';

    @Transactional
    @Override
    public void run() {


        //所有点赞过的topic
        Set<String> keys = redisCache.keys(TALK_THUMBS);
        log.info("talk点赞同步数据库开始");
        if(StringUtils.isNotEmpty(keys)){
            for (String key : keys) {//

                String[] split = key.split(":");
                Long talkId = Long.parseLong(split[split.length-1]);

                Set<ZSetOperations.TypedTuple<String>> cacheZSetWithScore = redisCache.getCacheZSetWithScore(key);

                thumbsService.remove(new LambdaQueryWrapper<TalkThumbs>().eq(TalkThumbs::getTalkId, talkId));
                List<TalkThumbs> collect = cacheZSetWithScore.stream().map(item -> TalkThumbs
                        .builder()
                        .talkId(talkId)
                        .userId(Long.parseLong(item.getValue()))
                        .createTime(new Date(item.getScore().longValue()))
                        .build())
                        .collect(Collectors.toList());

                thumbsService.saveBatch(collect);

                //删除缓存
                redisCache.deleteObject(key);
                log.info("同步talk:{}的点赞列表",talkId);
            }
        }
    }
}
