package com.xlf.auth.Authentication.wxAuthentication;

import com.xlf.auth.Authentication.JwtAuthentication.JwtAuthenticationFilter;
import com.xlf.auth.Handler.LoginFailHandler;
import com.xlf.auth.Handler.LoginSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


public class WxAuthenticationConfigurer extends AbstractHttpConfigurer<WxAuthenticationConfigurer, HttpSecurity> {


    @Override
    public void configure(HttpSecurity builder) throws Exception {

        // 拦截  /wxLogin 请求
        RequestMatcher matcher = new AntPathRequestMatcher("/wxLogin", null, true);

        AuthenticationManager localAuthManager = builder.getSharedObject(AuthenticationManager.class);

        WxAuthenticationFilter filter = new WxAuthenticationFilter(matcher, localAuthManager);
        filter.setAuthenticationSuccessHandler(new LoginSuccessHandler());
        filter.setAuthenticationFailureHandler(new LoginFailHandler());

        // 务必注意这里与配置类中声明的先后顺序
        builder.authenticationProvider(new WxAuthenticationProvider())
                .addFilterBefore(filter, JwtAuthenticationFilter.class);
    }
}
