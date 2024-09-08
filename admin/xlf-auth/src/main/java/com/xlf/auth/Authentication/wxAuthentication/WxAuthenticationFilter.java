package com.xlf.auth.Authentication.wxAuthentication;

import com.xlf.common.exception.auth.JwtAuthenticationException;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.common.util.spring.SpringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

public class WxAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    /**
     *
     * @param requiresAuthenticationRequestMatcher  拦截的登录路径
     * @param authenticationManager local的AuthenticationManager
     */
    protected WxAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher, AuthenticationManager authenticationManager) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);
    }

    /**
     *
     * 主要的任务就是调用具体的AuthenticationProvider进行身份验证。然后处理下面两种结果
     *
     * 认证失败：返回null将被过滤器拦截不做处理。如果抛异常（AuthenticationException父类）将被捕获然后调用 AuthenticationFailureHandler拦截处理
     *
     * 认证成功：构造并且返回携带信息的Authentication。并且调用AuthenticationSuccessHandler拦截处理
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //拿到前端传递的code
        String code = request.getParameter("code");

        //创建未认证的token
        WxToken wxToken = WxToken.unauthenticated(code);

        //寻找适合WxToken的AuthenticationProvider进行认证

        Authentication authenticate = this.getAuthenticationManager().authenticate(wxToken);


        return authenticate;
    }
}
