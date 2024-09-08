package com.xlf.auth.Authentication.wxAuthentication;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 小程序登录令牌。小程序登录携带获得的临时登录凭证 code过来。
 *
 */


public class WxToken extends AbstractAuthenticationToken {


    private static final long serialVersionUID = 1L;

    /**
     *用户凭证。小程序登录带来凭证code。
     */
    private Object credentials;

    /**
     * 用户信息
     */
    private  Object principal;

    /**
     * 创建成功的令牌
     * @param principal 用户信息
     * @param authorities 用户权限信息
     */
    public static WxToken authenticated(Object principal,Collection<? extends GrantedAuthority> authorities){
        return new WxToken(principal,authorities);
    }

    /**
     * 创建未认证的令牌
     * @param credentials 临时登录凭证
     */
    public static WxToken unauthenticated(Object credentials){
        return new WxToken(credentials);
    }


    public WxToken(Object principal,Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.setAuthenticated(true);
    }


    public WxToken(){
        super(null);
    }

    public WxToken(Object credentials) {
        super(null);
        this.principal = null;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }



    /**
     * 获取用户凭证信息，一般指密码。小程序登录没有密码，返回null
     * @return
     */
    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    /***
     * getPrincipal 获取用户身份信息，
     * 构造无认证的token:放用户唯一标识（如密码，这里指code)
     * 构造认证成功的token:一般就是认证成功后丢过来UserDetails
     * @return
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public String toString() {
        return "WxToken{" +
                "credentials=" + credentials +
                ", principal=" + principal +
                '}';
    }
}
