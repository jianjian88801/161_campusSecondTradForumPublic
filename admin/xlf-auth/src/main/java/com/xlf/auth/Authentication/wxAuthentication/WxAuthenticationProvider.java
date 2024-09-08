package com.xlf.auth.Authentication.wxAuthentication;

import com.xlf.auth.dao.AbstractAuthenticationProvider;
import com.xlf.auth.Enum.LoginTypeEnum;
import com.xlf.auth.strategy.context.SocialLoginStrategyContext;
import com.xlf.common.security.dao.JwtUserDetails;
import com.xlf.common.util.spring.SpringUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class WxAuthenticationProvider extends AbstractAuthenticationProvider {


    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, Authentication authentication) throws AuthenticationException {

    }

    @Override
    protected Authentication createSuccessAuthentication(JwtUserDetails user) {
        Authentication authentication = WxToken.authenticated(user, user.getAuthorities());
        return authentication;
    }

    @Override
    protected JwtUserDetails retrieveUser(Authentication authentication) throws AuthenticationException {

        //拿到code
        Object credentials = authentication.getCredentials();

        UserDetails userDetails = null;

        try {
            SocialLoginStrategyContext socialLoginStrategyContext = SpringUtils.getBean(SocialLoginStrategyContext.class);
            // 获取用户信息
            userDetails = socialLoginStrategyContext.executeLoginStrategy(credentials, LoginTypeEnum.WX);

        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalAuthenticationServiceException(e.getMessage());
        }

        return (JwtUserDetails) userDetails;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return WxToken.class.isAssignableFrom(authentication);
    }


}
