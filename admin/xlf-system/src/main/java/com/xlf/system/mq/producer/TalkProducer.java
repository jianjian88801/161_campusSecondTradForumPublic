package com.xlf.system.mq.producer;

import cn.hutool.json.JSONUtil;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.TalkComment;
import com.xlf.system.mq.dto.TalkCommentDTO;
import com.xlf.system.mq.dto.TalkDTO;
import com.xlf.system.domain.Talk;
import com.xlf.system.service.TalkService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xlf.common.constant.MqConstant.*;
import static com.xlf.common.constant.RedisConst.LOCK_TALK_COMMENT_SUBSCRIPTION;
import static com.xlf.common.constant.RedisConst.TALK_COMMENT_SUBSCRIPTION;

@Service
@Data
@Slf4j
public class TalkProducer {


    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    RedisCache redisCache;

    @Resource
    RedissonClient redissonClient;


//    @Value(value = "${template.talkCommentTemplateId}")
//    private String template_id;


    @PostConstruct //构造方法后执行它，相当于初始化作用
    public void init(){
        rabbitTemplate.setConfirmCallback(
                //lambda 表达式
                (correlationData, ack, cause)->{
                    if (!ack){
                        log.info("talkId：{}",correlationData.getId()+"");
                        log.error("消息没有到达交换机，原因为：{}",cause);
                    }
                }

        );
    }

    /**
     *消息投递
     * @param
     */
    public void sendMsg(Talk talk){
        TalkDTO talkDTO = new TalkDTO();
        talkDTO.setId(talk.getId());
        talkDTO.setUserId(talk.getUserId());
        talkDTO.setCreateTime(talk.getCreateTime());
        String msg = JSONUtil.toJsonStr(talkDTO);
        Message message= MessageBuilder.withBody(msg.getBytes()).build();
        CorrelationData correlationData=new CorrelationData(); //关联数据
        correlationData.setId(talk.getId().toString()); //唯一标识

        rabbitTemplate.convertAndSend(TALK_DELIVERY_EXCHANGE,ARTICLE_KEY,message,correlationData);

    }

    /**
     * 通知用户有人回复它了
     * @param
     */
    public void sendMsg(TalkComment talkComment){

        String key =  TALK_COMMENT_SUBSCRIPTION + talkComment.getTalkId();
        //不存在有人订阅
        if(!redisCache.exist(key))
            return;

        //存在有人订阅
        RLock lock = redissonClient.getLock(LOCK_TALK_COMMENT_SUBSCRIPTION + talkComment.getTalkId());

        if(lock.tryLock()){
            Set<String> userNameList = null;
            try {

                userNameList = redisCache.getCacheSet(key);
                if(StringUtils.isEmpty(userNameList))
                    return;
                //这里如果有人在删除前订阅的话会丢失数据
               // redisCache.deleteObject(TALK_COMMENT_SUBSCRIPTION + talkComment.getTalkId());
                //方法二：一个一个删
                //直接传set删除不了
                userNameList.forEach(item->{
                   redisCache.removeSetValue(key, item);
                });

            } catch (AmqpException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = format.format(talkComment.getCreateTime());
            TalkCommentDTO talkCommentDTO = TalkCommentDTO.builder()
                    .id(talkComment.getId())
                    .talkId(talkComment.getTalkId())
                    .content(talkComment.getContent().substring(0,Math.min(18,talkComment.getContent().length())))
                    .nickName(talkComment.getUser().getNickName())
                    .openIds(userNameList)
                    .data(dateStr)
                    .build();

            //mq
            String msg = JSONUtil.toJsonStr(talkCommentDTO);
            Message message= MessageBuilder.withBody(msg.getBytes()).build();
            CorrelationData correlationData = new CorrelationData(); //关联数据
            correlationData.setId(talkComment.getId().toString()); //唯一标识
            rabbitTemplate.convertAndSend(SUBSCRIPTION_EXCHANGE,TALK_COMMENT_SUBSCRIPTION_KEY,message,correlationData);

        }

    }






}
