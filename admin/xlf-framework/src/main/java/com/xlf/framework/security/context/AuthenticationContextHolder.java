package com.xlf.framework.security.context;

import org.springframework.security.core.Authentication;

/**
 * 身份验证信息,把认证放入ThreadLocal中，离开线程记得清空
 *
 * @author ruoyi
 */
public class AuthenticationContextHolder
{
    private static final ThreadLocal<Authentication> contextHolder = new ThreadLocal<>();

    public static Authentication getContext()
    {
        return contextHolder.get();
    }

    public static void setContext(Authentication context)
    {
        contextHolder.set(context);
    }

    public static void clearContext()
    {
        contextHolder.remove();
    }
}

