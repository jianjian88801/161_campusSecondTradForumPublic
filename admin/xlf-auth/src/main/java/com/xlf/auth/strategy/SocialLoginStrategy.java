package com.xlf.auth.strategy;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * 第三方登录策略
 *
 */
public interface SocialLoginStrategy {

    /**
     * 登录
     *
     * @param data 数据
     * @return {@link Map} 用户信息
     */
    UserDetails login(Object data);

}
