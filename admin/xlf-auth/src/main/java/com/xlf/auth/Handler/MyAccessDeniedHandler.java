package com.xlf.auth.Handler;

import com.alibaba.fastjson.JSON;
import com.xlf.common.pojo.mode.AjaxResult;
import com.xlf.common.util.ResponseResult;
import com.xlf.common.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足拦截，在ExceptionTranslationFilter委托过来
 * 抛403
 */

public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException){

        AjaxResult ajaxResult = new AjaxResult(HttpStatus.FORBIDDEN.value(), "您权限不足，请联系管理员授权！");

        WebUtils.renderString(response,JSON.toJSONString(ajaxResult));
    }
}
