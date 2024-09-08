package com.xlf.auth.Handler;

import com.alibaba.fastjson.JSON;
import com.xlf.common.security.dao.JwtUserDetails;
import com.xlf.common.security.util.JwtTokenService;
import com.xlf.common.constant.Constants;
import com.xlf.common.pojo.mode.AjaxResult;
import com.xlf.common.util.ServletUtils;
import com.xlf.common.util.spring.SpringUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功后处理器，任务是构建token丢给前端。AbstractAuthenticationProcessingFilter登录认证成功后会委托过来
 * 如果抛异常会被LoginFailHandler捕获拦截。
 * 这里使用Component交给spring管理，如果有多个AuthenticationSuccessHandler请注意bean冲突
 */
public class LoginSuccessHandler implements AuthenticationSuccessHandler {


    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId)
    {

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {



        //构建token返回出去
        Object principal = authentication.getPrincipal();

        JwtTokenService tokenService = SpringUtils.getBean(JwtTokenService.class);

        if(principal instanceof JwtUserDetails) {
            try {
                String token =  tokenService.createToken((JwtUserDetails) principal);
                AjaxResult ajax = AjaxResult.success();
                ajax.put(Constants.TOKEN, token);
                ServletUtils.renderString(response, JSON.toJSONString(ajax));
            } catch (Exception e) {
                //创建令牌出问题了
                e.printStackTrace();
                throw new InternalAuthenticationServiceException("create jwt is fail!",e);
            }
        }else {
            throw new InternalAuthenticationServiceException("principal is no JwtUserDetails!");

        }


    }
}
