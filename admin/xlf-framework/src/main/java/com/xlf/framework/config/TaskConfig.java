package com.xlf.framework.config;

import com.xlf.framework.task.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Date;


/**
 * 定时任务配置
 */
@Configuration
@Data
@Slf4j
public class TaskConfig implements SchedulingConfigurer {

    @Autowired
    UpdateTopicViewTask updateTopicViewTask;
    @Autowired
    UpdateTopicThumbsTask updateTopicThumbsTask;
    @Autowired
    UpdateUserTopicCollection updateUserTopicCollection;
    @Autowired
    UpdateTalkThumbsTask updateTalkThumbsTask;
    @Autowired
    UpdateUserTalkCollection updateUserTalkCollection;
    @Autowired
    UpdateUserFollowTask updateUserFollowTask;
    @Autowired
    UpdateUserFansTask updateUserFansTask;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        log.info("--- 任务注册开始 ---");
        taskRegistrar.addCronTask(updateTopicViewTask,"0 0 5 * * ?");
        taskRegistrar.addCronTask(updateTopicThumbsTask,"0 10 5 * * ?");
        taskRegistrar.addCronTask(updateUserTopicCollection,"0 20 5 * * ?");


        taskRegistrar.addCronTask(updateTalkThumbsTask,"0 30 5 * * ?");
        taskRegistrar.addCronTask(updateUserTalkCollection,"0 40 5 * * ?");


        taskRegistrar.addCronTask(updateUserFansTask,"0 50 5 * * ?");
        taskRegistrar.addCronTask(updateUserFollowTask,"0 55 5 * * ?");
        log.info("--- 任务注册结束 ---");
    }
}
