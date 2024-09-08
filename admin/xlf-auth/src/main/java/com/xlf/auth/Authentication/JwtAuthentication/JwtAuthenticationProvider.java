package com.xlf.auth.Authentication.JwtAuthentication;

import com.xlf.auth.dao.AbstractAuthenticationProvider;
import com.xlf.common.constant.HttpStatus;
import com.xlf.common.exception.auth.JwtAuthenticationException;
import com.xlf.common.security.dao.JwtUserDetails;
import com.xlf.common.security.util.JwtTokenService;
import com.xlf.common.util.spring.SpringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtAuthenticationProvider extends AbstractAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, Authentication authentication) throws AuthenticationException {

    }

    @Override
    protected Authentication createSuccessAuthentication(JwtUserDetails userDetails) {

        JwtAuthenticationToken authenticated = JwtAuthenticationToken.authenticated(userDetails, userDetails.getAuthorities());
        return authenticated;
    }

    /**
     * 获取用户信息
     * @param authentication 未认证的令牌
     * @return 用户信息
     * @throws AuthenticationException
     */
    @Override
    protected JwtUserDetails retrieveUser(Authentication authentication) throws AuthenticationException {

        JwtTokenService jwtTokenService = SpringUtils.getBean(JwtTokenService.class);


        JwtUserDetails jwtUserDetails = jwtTokenService.getAuthentication((String) authentication.getCredentials());

        //拿不到身份信息
        if(jwtUserDetails==null){
            throw new JwtAuthenticationException("登录过期，请重新登录！", HttpStatus.UNAUTHORIZED);
        }
        //续命：
        jwtTokenService.verifyToken(jwtUserDetails);

        return jwtUserDetails;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
