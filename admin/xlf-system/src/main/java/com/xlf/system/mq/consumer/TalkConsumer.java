package com.xlf.system.mq.consumer;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.Talk;
import com.xlf.system.domain.TalkComment;
import com.xlf.system.mq.builder.SubscribeService;
import com.xlf.system.mq.dto.TalkCommentDTO;
import com.xlf.system.mq.dto.TalkDTO;
import com.xlf.system.service.RedisCacheBuilder;
import com.xlf.system.service.TalkService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static com.xlf.common.constant.Constants.getUserDropBox;
import static com.xlf.common.constant.Constants.getUserFans;
import static com.xlf.common.constant.MqConstant.*;

@Service
@Slf4j
public class TalkConsumer {


    @Resource
    RedisCache redisCache;

    @Resource
    RedisCacheBuilder redisCacheBuilder;

    @Resource
    SubscribeService subscribeService;

    @Resource
    TalkService talkService;


    /**
     * 把talk推送给我的粉丝
     */
    @RabbitListener(queues = {TALK_DELIVERY_QUEUE})
    private void deliveryConsumer(Message message) {

        try {
            //我的粉丝列表
            String data = new String(message.getBody(), StandardCharsets.UTF_8);
            TalkDTO talk = JSONUtil.toBean(JSONUtil.toJsonStr(data), TalkDTO.class);
            Long userId = talk.getUserId();
            String key = getUserFans(userId.toString());
           // log.info("消息投递-> 帖子：{},用户：{},时间：{}",talk.getId(),talk.getUserId(),talk.getCreateTime());
            /**
             * 拿到我的粉丝列表
             */
            Set<String> cacheZSet =  redisCache.queryWithMutexAndBuild(key,(k)-> redisCache.getCacheZSet(k),(k)->{
                redisCacheBuilder.buildFansCache(userId,k);
                return redisCache.getCacheZSet(k);
            });
            /**
             * 发到所有粉丝的投递箱
             */
            if(StringUtils.isNotEmpty(cacheZSet)){
                cacheZSet.forEach(item->{
                    String userDropBoxKey = getUserDropBox(item);
                    redisCache.consumerWithMutexAndBuild(userDropBoxKey,(k)->{
                        redisCache.setCacheZSet(k, talk.getId().toString(), talk.getCreateTime().getTime());
                    },(k)->{
                        redisCacheBuilder.buildUserDropBox(Long.parseLong(item),k);
                    });
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("投递失败");
        }
    }

    /**
     * 消费订阅talk评论的用户
     * @param message
     */
    @RabbitListener(queues = {TALK_COMMENT_SUBSCRIPTION_QUEUE})
    private void subscriptionTalkCommentConsumer(Message message){
        try {
            String data = new String(message.getBody(), StandardCharsets.UTF_8);
            TalkCommentDTO talkComment = JSONUtil.toBean(JSONUtil.toJsonStr(data), TalkCommentDTO.class);
//            log.info("消费{}",talkComment);
            String content = talkService.getOne(new LambdaQueryWrapper<Talk>()
                    .eq(Talk::getId, talkComment.getTalkId())
                    .select(Talk::getContent))
                    .getContent();
            content = content.substring(0,Math.min(18,content.length()));
            talkComment.setTalkContent(content);

            subscribeService.SendTalkCommentSubscriptionMessage(talkComment);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送订阅失败");

        }
    }

}
