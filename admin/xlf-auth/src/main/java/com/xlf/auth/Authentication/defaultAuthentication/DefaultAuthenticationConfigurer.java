package com.xlf.auth.Authentication.defaultAuthentication;

import com.xlf.auth.Authentication.JwtAuthentication.JwtAuthenticationFilter;
import com.xlf.auth.Handler.LoginFailHandler;
import com.xlf.auth.Handler.LoginSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 默认账号密码登录。禁用掉UsernamePasswordAuthenticationFilter
 */
public class DefaultAuthenticationConfigurer extends AbstractHttpConfigurer<DefaultAuthenticationConfigurer, HttpSecurity> {



    @Override
    public void configure(HttpSecurity builder) {

        // 拦截 POST /login 请求
        RequestMatcher matcher = new AntPathRequestMatcher("/login", null, true);

        AuthenticationManager localAuthManager = builder.getSharedObject(AuthenticationManager.class);

        DefaultAuthenticationFilter filter = new DefaultAuthenticationFilter(matcher,localAuthManager);
        filter.setAuthenticationSuccessHandler(new LoginSuccessHandler());
        filter.setAuthenticationFailureHandler(new LoginFailHandler());

       // 务必注意这里与配置类中声明的先后顺序
        builder.addFilterBefore(filter, JwtAuthenticationFilter.class);
    }
}
