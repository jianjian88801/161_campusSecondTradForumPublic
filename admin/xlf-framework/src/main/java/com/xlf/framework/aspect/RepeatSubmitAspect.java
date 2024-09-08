package com.xlf.framework.aspect;

import com.xlf.common.annotation.RepeatSubmit;
import com.xlf.common.constant.CacheConstants;
import com.xlf.common.pojo.mode.AjaxResult;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.common.util.ServletUtils;
import com.xlf.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * 防止同一个用户重复提交（只能已登录有token的用户使用）。因为要使用token来标识同一用户
 * @author xlf
 */
@Aspect
@Slf4j
@Component
public class RepeatSubmitAspect {

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    @Resource
    private RedisCache redisCache;


    @Pointcut("@annotation(com.xlf.common.annotation.RepeatSubmit)")
    private void RepeatSubmit(){
    }


    @Around("RepeatSubmit()")
    public AjaxResult around(ProceedingJoinPoint joinPoint) throws Throwable {


        //1.获取方法上的注解
        Class<?> targetCls=joinPoint.getTarget().getClass();
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Method targetMethod=targetCls.getDeclaredMethod(signature.getName(),signature.getParameterTypes());
        RepeatSubmit repeatSubmit = targetMethod.getAnnotation(RepeatSubmit.class);

        //2.获取方法参数信息
        Object[] args = joinPoint.getArgs();
        String params = Arrays.toString(args); //eg: params = [xxx,xxx,xxx]

        //3.生成redis的key值
        HttpServletRequest request = ServletUtils.getRequest();
        // 请求地址（作为存放cache的key值）
        String url = request.getRequestURI();
        System.out.println(url);
        // 标识同一个人操作的唯一值（这里使用用户登录后的token）
        String submitKey = StringUtils.trimToEmpty(request.getHeader(header));

        //唯一标识
        String cacheRepeatKey = CacheConstants.REPEAT_SUBMIT_KEY + url +'/'+ submitKey + "/" + params ;

        //4.尝试提交
       if(redisCache.setIfAbsent(cacheRepeatKey, true, repeatSubmit.interval(), TimeUnit.MILLISECONDS)){

          return (AjaxResult) joinPoint.proceed();

       //已经有人提交过了
       }else {
           return AjaxResult.error(repeatSubmit.message());
       }

    }

}
