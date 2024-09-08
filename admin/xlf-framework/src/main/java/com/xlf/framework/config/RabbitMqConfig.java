package com.xlf.framework.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.xlf.common.constant.MqConstant.*;

@Configuration
public class RabbitMqConfig {


    /**
     * 定义粉丝投递信息交换机
     * @return 直接交换机
     */
    @Bean
    DirectExchange talkExchange(){
        return ExchangeBuilder
                .directExchange(TALK_DELIVERY_EXCHANGE)
                .durable(true) //持久化
                .build();
    }

    /**
     *粉丝投递信息队列
     * @return
     */
    @Bean
    Queue talkQueue(){
        return QueueBuilder
                .durable(TALK_DELIVERY_QUEUE) //持久化
                .build();
    }

    /**
     * 绑定
     */
    @Bean
    public Binding talkQueueBinding() {
        return BindingBuilder.bind(talkQueue()).to(talkExchange()).with(ARTICLE_KEY);
    }




    /**
     * 小程序用户订阅的交换机
     * @return 直接交换机
     */
    @Bean
    DirectExchange subscriptionExchange(){
        return ExchangeBuilder
                .directExchange(SUBSCRIPTION_EXCHANGE)
                .durable(true) //持久化
                .build();
    }

    /**
     * 帖子评论队列
     * @return
     */
    @Bean
    Queue talkCommentSubscriptionQueue(){
        return QueueBuilder
                .durable(TALK_COMMENT_SUBSCRIPTION_QUEUE) //持久化
                .build();
    }

    /**
     * 绑定
     */
    @Bean
    public Binding talkCommentSubscriptionQueueBinding() {
        return BindingBuilder.bind(talkCommentSubscriptionQueue()).to(subscriptionExchange()).with(TALK_COMMENT_SUBSCRIPTION_KEY);
    }




}
