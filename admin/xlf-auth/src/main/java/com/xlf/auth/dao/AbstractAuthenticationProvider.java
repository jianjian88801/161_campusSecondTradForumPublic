
package com.xlf.auth.dao;

import com.xlf.common.security.dao.JwtUserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * author:xlf
 * 认证：系统异常，账号找不到，密码错误，禁用。token过期（凭证过期）
 *
 */
public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

    protected abstract void additionalAuthenticationChecks(UserDetails userDetails, Authentication authentication) throws AuthenticationException;


    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        JwtUserDetails user = null;
        try {
            //获取用户信息
            user = this.retrieveUser(authentication);
        } catch (UsernameNotFoundException var6) { //账号找不到
            throw new UsernameNotFoundException("账号不存在");
        }

        //前置验证，验证是否被禁用
        try {
            this.preAuthenticationChecks.check(user);
            //其他验证，比如密码验证
            this.additionalAuthenticationChecks(user, authentication);

            //后置验证，验证登录是否过期
         //   this.postAuthenticationChecks.check(user);
        } catch (AuthenticationException var7) {
            throw var7;
        } catch (Exception e){
            throw new AuthenticationServiceException("系统错误");
        }
        //创建成功登录的令牌
        return this.createSuccessAuthentication(user);
    }

    private String determineUsername(Authentication authentication) {
        return authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();
    }

    protected abstract Authentication createSuccessAuthentication(JwtUserDetails user);

    protected abstract JwtUserDetails retrieveUser(Authentication authentication) throws AuthenticationException;

    protected UserDetailsChecker getPreAuthenticationChecks() {
        return this.preAuthenticationChecks;
    }

    public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
        this.preAuthenticationChecks = preAuthenticationChecks;
    }

    protected UserDetailsChecker getPostAuthenticationChecks() {
        return this.postAuthenticationChecks;
    }

    public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
        private DefaultPostAuthenticationChecks() {
        }

        public void check(UserDetails user) {
            //凭证过期
            if (!user.isCredentialsNonExpired()) {
                throw new CredentialsExpiredException("登录过期，请重新登录");
            }
        }
    }

    private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
        private DefaultPreAuthenticationChecks() {
        }

        public void check(UserDetails user) {
            //锁定
            if (!user.isAccountNonLocked()) {
                throw new LockedException("账号被锁定，请联系管理员！");
            //禁用，被管理员禁用
            } else if (!user.isEnabled()) {
                throw new DisabledException("账号被禁用，请联系管理员！");
           //账号过期
            } else if (!user.isAccountNonExpired()) {
                throw new AccountExpiredException("账号过期！");
            }
        }
    }
}
