package com.xlf.auth.Authentication.defaultAuthentication;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login", "POST");

    private String usernameParameter = "username";
    private String passwordParameter = "password";
    private boolean postOnly = false;
    //验证码功能
    private boolean verification = false;

    public DefaultAuthenticationFilter() {


        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public DefaultAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public DefaultAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher,AuthenticationManager authenticationManager) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);
    }

    /**
     *
     * 主要的任务就是调用具体的AuthenticationProvider进行身份验证。然后处理下面两种结果
     *
     * 认证失败：返回null将被过滤器拦截不做处理。如果抛异常（AuthenticationException父类）将被捕获然后调用 AuthenticationFailureHandler拦截处理
     *
     * 认证成功：返回携带信息的Authentication。并且调用AuthenticationSuccessHandler拦截处理
     *
     * AuthenticationProvider会捕获异常然后重新抛出
     * AuthenticationManager不会处理异常（只抛出AuthenticationProvider的异常）。
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST"))
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());

        if(this.verification){
            this.verification();
        }

        String username = this.obtainUsername(request);
        username = username != null ? username.trim() : "";
        String password = this.obtainPassword(request);
        password = password != null ? password : "";
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        //设置authRequest的Details 其实就是WebAuthenticationDetails。包括sessionId和remoteAddress
        this.setDetails(request, authRequest);

        Authentication authenticate = null;

        authenticate = this.getAuthenticationManager().authenticate(authRequest);

        return authenticate;

    }

    public void verification(){

    }

    @Nullable
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(this.passwordParameter);
    }

    @Nullable
    protected String obtainUsername(HttpServletRequest request) {


        return request.getParameter(this.usernameParameter);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return this.usernameParameter;
    }

    public final String getPasswordParameter() {
        return this.passwordParameter;
    }
}
