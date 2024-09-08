package com.xlf.auth.Authentication.JwtAuthentication;

import com.xlf.auth.Authentication.wxAuthentication.WxToken;
import com.xlf.common.security.dao.JwtUserDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {



    private static final long serialVersionUID = 1L;

    /**
     *用户凭证
     */
    private Object credentials;

    /**
     * 用户信息
     */
    private  Object principal;


    public JwtAuthenticationToken(JwtUserDetails principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.setAuthenticated(true);
        this.principal = principal;

    }

    public JwtAuthenticationToken(Object credentials) {
        super(null);
        this.setAuthenticated(false);
        this.credentials = credentials;

    }

    /**
     * 创建成功的令牌
     * @param principal 用户信息
     * @param authorities 用户权限信息
     */
    public static JwtAuthenticationToken authenticated(JwtUserDetails principal, Collection<? extends GrantedAuthority> authorities){
        return new JwtAuthenticationToken(principal,authorities);
    }

    /**
     * 创建未认证的令牌
     * @param principal 登录凭证(传递token过来)
     */
    public static JwtAuthenticationToken unauthenticated(Object principal){
        return new JwtAuthenticationToken(principal);
    }



    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
