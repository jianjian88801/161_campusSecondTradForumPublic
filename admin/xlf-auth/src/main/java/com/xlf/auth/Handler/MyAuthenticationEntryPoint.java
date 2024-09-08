package com.xlf.auth.Handler;

import com.alibaba.fastjson.JSON;
import com.xlf.common.pojo.mode.AjaxResult;
import com.xlf.common.util.ResponseResult;
import com.xlf.common.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败拦截，在ExceptionTranslationFilter委托过来。原因是没有认证（从springSecurity上下文拿不到已认证的authentication）被springSecurity拦截了。
 *
 * 抛401让用户重新登录
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        //给前端ResponseResult 的json
        AjaxResult ajaxResult = new AjaxResult(HttpStatus.UNAUTHORIZED.value(), "请登录后访问！");
        String json = JSON.toJSONString(ajaxResult);
        WebUtils.renderString(response,json);
    }
}
