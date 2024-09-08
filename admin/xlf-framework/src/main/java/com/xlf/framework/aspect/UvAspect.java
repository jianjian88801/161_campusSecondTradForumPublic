package com.xlf.framework.aspect;

import com.xlf.common.util.redis.RedisCache;
import com.xlf.common.util.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.xlf.common.constant.TopicConstants.TOPIC_VIEW;

@Component
@Aspect
@Slf4j
public class UvAspect {

    @Resource
    private RedisCache redisService;

    // 拿到@UVlog注解注释的方法，这里就是切点
    @Pointcut("@annotation(com.xlf.common.annotation.UVlog)")
    private void weblog(){
    }

    // 调用方法后都会进行统计操作，写入redis
    @After("weblog()")
    public void afterMethod(JoinPoint joinpoint) {

        HttpServletRequest request = ServletUtils.getRequest();

        Object[] args = joinpoint.getArgs();


        String key = TOPIC_VIEW+':'+args[0];

         redisService.increment(key);
    }
}
