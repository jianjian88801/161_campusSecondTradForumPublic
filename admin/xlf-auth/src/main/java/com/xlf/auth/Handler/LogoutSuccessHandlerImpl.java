package com.xlf.auth.Handler;


import com.alibaba.fastjson.JSON;
import com.xlf.common.security.dao.JwtUserDetails;
import com.xlf.common.security.util.JwtTokenService;
import com.xlf.common.pojo.mode.AjaxResult;
import com.xlf.common.util.ServletUtils;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.spring.SpringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义退出处理类（/logout） 返回成功
 *
 * @author
 */
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler
{

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    {

        Object principal = null;
        try {
            principal = authentication.getPrincipal();

            JwtTokenService tokenService = SpringUtils.getBean(JwtTokenService.class);

            if (StringUtils.isNotNull(principal))  // 删除用户在redis的缓存记录
            {
                if(principal instanceof JwtUserDetails)
                    tokenService.delAuthentication(((JwtUserDetails)principal).getUuid());
            }
        } catch (Exception e) {

        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success("退出成功")));
    }
}
