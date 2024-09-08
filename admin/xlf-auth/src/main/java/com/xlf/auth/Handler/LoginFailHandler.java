package com.xlf.auth.Handler;

import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import com.xlf.common.constant.HttpStatus;
import com.xlf.common.exception.auth.DaoAuthUserDetailsException;
import com.xlf.common.exception.auth.JwtAuthenticationException;
import com.xlf.common.pojo.mode.AjaxResult;
import com.xlf.common.util.ServletUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录认证失败处理器，AbstractAuthenticationProcessingFilter登录认证失败会委托过来，即我们写的登录认证去实现filterAbstractAuthenticationProcessingFilter。
 *
 *把登录认证失败的原因抛给用户。验证码错误?用户名错误?密码错误?用户被禁用？抛的错误类型由具体的业务传递过来
 * //状态码不要设401,因为前端如果拦截的话会跳去登录界面，现在用户已经是在登录中了。
 */
public class LoginFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)  {


        AjaxResult err = null;

        if(e instanceof BadCredentialsException || e instanceof UsernameNotFoundException){

            err =  AjaxResult.error(HttpStatus.ERROR,"账号或密码错误！");

        }else if(e instanceof LockedException){

            //验证码错误?用户名错误?用户被禁用?
            err =  AjaxResult.error(HttpStatus.ERROR,"账号被锁定");

        }else if(e instanceof DisabledException) {

            err =  AjaxResult.error(HttpStatus.ERROR,"账号被停用");

        }else if(e instanceof AccountExpiredException){

            err =  AjaxResult.error(HttpStatus.ERROR,"账号被删除");

        }else if(e instanceof CredentialsExpiredException){

            err =  AjaxResult.error(HttpStatus.ERROR,"凭证过期");

        }else if(e instanceof JwtAuthenticationException) {

                err = AjaxResult.error(HttpStatus.ERROR,e.getMessage());

        }
        else{
            err = AjaxResult.error(HttpStatus.ERROR,"系统错误，请联系管理员！");
        }

        ServletUtils.renderString(response,JSON.toJSONString(err));

    }
}
