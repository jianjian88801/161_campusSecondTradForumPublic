package com.xlf.framework.task;


import com.xlf.common.constant.TopicConstants;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.Topic;
import com.xlf.system.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 同步redis的访问量到数据库
 */
@Slf4j
@Service
public class UpdateTopicViewTask implements Runnable{

    @Resource
    TopicService topicService;

    @Autowired
    RedisCache redisCache;

    @Override
    public void run() {
        //id,
        Map<String, Integer> cacheMap = redisCache.getCacheMap(TopicConstants.TOPIC_VIEW);
      //  log.info("任务开始");
        if(cacheMap!=null)
        {
            cacheMap.forEach((k,v)->{
                try {
                    if(k!="-1")
                          topicService.lambdaUpdate().set(Topic::getView, v).eq(Topic::getId, k).update();
                    log.info("更新topic："+k+"浏览量成功");
                } catch (Exception e) {
                    log.warn("更新topic："+k+"浏览量失败");
                }
            });
        }
    }
}
