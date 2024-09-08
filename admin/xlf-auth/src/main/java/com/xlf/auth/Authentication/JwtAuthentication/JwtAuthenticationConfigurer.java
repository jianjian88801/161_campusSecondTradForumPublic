package com.xlf.auth.Authentication.JwtAuthentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * 配置拦截所有需要认证的请求
 */
public class JwtAuthenticationConfigurer  extends AbstractHttpConfigurer<JwtAuthenticationConfigurer, HttpSecurity> {


    @Override
    public void configure(HttpSecurity builder) throws Exception {

        //放行的路径
//        CommentMatcher commentMatcher = new CommentMatcher();
//        commentMatcher.antMatchers("/login","/wxLogin","/index");

        AuthenticationManager localAuthManager = builder.getSharedObject(AuthenticationManager.class);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(localAuthManager);

        //
        builder.authenticationProvider(new JwtAuthenticationProvider())
                .addFilterAfter(filter, LogoutFilter.class);
    }
}
