package com.xlf.framework.security.config;

import com.xlf.auth.Authentication.JwtAuthentication.JwtAuthenticationConfigurer;
import com.xlf.auth.Authentication.defaultAuthentication.DefaultAuthenticationConfigurer;
import com.xlf.auth.Authentication.wxAuthentication.WxAuthenticationConfigurer;
import com.xlf.auth.Handler.LogoutSuccessHandlerImpl;
import com.xlf.auth.Handler.MyAccessDeniedHandler;
import com.xlf.auth.Handler.MyAuthenticationEntryPoint;

import com.xlf.common.util.spring.SpringUtils;
import com.xlf.framework.security.service.DaoUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * 加解密bean
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }





    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF禁用，因为不使用session
                .csrf().disable()
                // 禁用HTTP响应标头
                .headers().cacheControl().disable().and()
                .headers().frameOptions().disable().and()
                //禁用表单登录，即不需要使用UsernamePasswordAuthenticationFilter
                .formLogin().disable()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 过滤请求
                .authorizeRequests()
//                .antMatchers("/test/**").permitAll()
                // 对于登录login 注册register 验证码captchaImage 允许匿名访问
                .antMatchers("/**","/login","/wxLogin", "/register", "/captcha").permitAll()

                // 静态资源，可匿名访问
                .antMatchers(HttpMethod.GET, "/", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/profile/**","/preview/file/**").permitAll()
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/*/api-docs", "/druid/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();


        // 添加Logout filter
        http.logout().logoutUrl("/logout").logoutSuccessHandler(new LogoutSuccessHandlerImpl());

        //添加认证失败、权限不足处理拦截器
        http.exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler());

        //添加认证方式
        http.apply(new JwtAuthenticationConfigurer()).and() //jwt认证
                .apply(new DefaultAuthenticationConfigurer()).and().userDetailsService(SpringUtils.getBean(DaoUserDetailsService.class)) //账号密码认证
                .apply(new WxAuthenticationConfigurer()).and();//微信认证

        //允许跨域
        http.cors();
        return http.build();
    }

    @Resource
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        return authenticationManager;
    }

}
