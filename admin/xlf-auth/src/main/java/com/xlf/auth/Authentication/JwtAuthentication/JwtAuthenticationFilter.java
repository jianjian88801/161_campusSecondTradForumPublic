package com.xlf.auth.Authentication.JwtAuthentication;

import cn.hutool.core.util.ObjectUtil;
import com.xlf.auth.Authentication.JwtAuthentication.handler.JwtAuthenticationFailHandler;
import com.xlf.auth.Authentication.JwtAuthentication.handler.JwtAuthenticationSuccessHandler;
import com.xlf.common.constant.HttpStatus;
import com.xlf.common.exception.auth.JwtAuthenticationException;
import com.xlf.common.security.dao.JwtUserDetails;
import com.xlf.common.security.util.JwtTokenService;
import com.xlf.common.security.util.SecurityUtils;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.spring.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt拦截器
 */

public class JwtAuthenticationFilter  extends OncePerRequestFilter {


    private AuthenticationManager authenticationManager;

    private RequestMatcher requiresAuthenticationRequestMatcher = AnyRequestMatcher.INSTANCE;

    private AuthenticationSuccessHandler successHandler = new JwtAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new JwtAuthenticationFailHandler();


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        if (!this.requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
        } else {
            try {
                Authentication authenticationResult = this.attemptAuthentication(request, response);

                if(authenticationResult!=null)
                   this.successfulAuthentication(request, response, chain, authenticationResult);

                chain.doFilter(request, response);

                //登录过期，放行
            } catch (JwtAuthenticationException e){
                chain.doFilter(request, response);
            }
            catch (InternalAuthenticationServiceException var5) {
                this.logger.error("An internal error occurred while trying to authenticate the user.", var5);
                this.unsuccessfulAuthentication(request, response, var5);
            } catch (AuthenticationException var6) {
                this.unsuccessfulAuthentication(request, response, var6);
            }
        }
    }


    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
//        this.logger.trace("Failed to process authentication request", failed);
        this.failureHandler.onAuthenticationFailure(request, response, failed);
    }

    /**
     * 有token就认证
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        JwtTokenService jwtTokenService = SpringUtils.getBean(JwtTokenService.class);

        String token = jwtTokenService.getToken(request);

        //用户请求头没带token，放行。
        if(StringUtils.isNull(token)){
            return null;
        }


        JwtAuthenticationToken unauthenticated = JwtAuthenticationToken.unauthenticated(token);

        Authentication authenticate = null;

        //有token去认证，返回认证成功的token
        authenticate = this.getAuthenticationManager().authenticate(unauthenticated);

        return authenticate;
    }


    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        //设置线程内的上下文给线程使用。后面security的拦截器会根据有没有context来判断是否已经认证及鉴权。
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
      //  SecurityContextHolder.getContext().setAuthentication(authResult);

    }



    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (this.requiresAuthenticationRequestMatcher.matches(request)) {
            return true;
        } else {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(LogMessage.format("Did not match request to %s", this.requiresAuthenticationRequestMatcher));
            }

            return false;
        }
    }



    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public RequestMatcher getRequiresAuthenticationRequestMatcher() {
        return requiresAuthenticationRequestMatcher;
    }

    public void setRequiresAuthenticationRequestMatcher(RequestMatcher requiresAuthenticationRequestMatcher) {
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
    }

    public AuthenticationSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    public AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

}
