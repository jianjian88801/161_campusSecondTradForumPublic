package com.xlf.framework.security.service;

import com.xlf.common.constant.CacheConstants;
import com.xlf.common.exception.user.CaptchaException;
import com.xlf.common.exception.user.CaptchaExpireException;
import com.xlf.common.util.ip.IpUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.common.util.ServletUtils;
import com.xlf.common.util.StringUtils;
import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 登录校验方法
 *
 * @author
 */
@Component
public class SysLoginService
{


    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysUserService userService;




    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId)
    {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        sysUser.setLoginDate(new Date());
        userService.updateById(sysUser);
    }
}
