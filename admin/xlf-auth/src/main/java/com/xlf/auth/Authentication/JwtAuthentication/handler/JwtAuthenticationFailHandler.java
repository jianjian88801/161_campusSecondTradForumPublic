package com.xlf.auth.Authentication.JwtAuthentication.handler;

import com.alibaba.fastjson.JSON;
import com.xlf.common.constant.HttpStatus;
import com.xlf.common.exception.auth.JwtAuthenticationException;
import com.xlf.common.pojo.mode.AjaxResult;
import com.xlf.common.util.ServletUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthenticationFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)  {

        AjaxResult err = null;

        if(e instanceof JwtAuthenticationException){

            err =  AjaxResult.error(HttpStatus.UNAUTHORIZED,"登录过期，请重新登录");
        }else if(e instanceof LockedException){

            //验证码错误?用户名错误?用户被禁用?
            err =  AjaxResult.error(HttpStatus.ERROR,"账号被锁定");

        }else if(e instanceof DisabledException) {

            err =  AjaxResult.error(HttpStatus.ERROR,"账号被停用");

        }else if(e instanceof AccountExpiredException){

            err =  AjaxResult.error(HttpStatus.ERROR,"账号被删除");

        }else if(e instanceof CredentialsExpiredException){

            err =  AjaxResult.error(HttpStatus.ERROR,"凭证过期");
        }else {

            err = AjaxResult.error(HttpStatus.ERROR,"系统错误，请联系管理员！");
        }

        ServletUtils.renderString(response,JSON.toJSONString(err));
    }
}
