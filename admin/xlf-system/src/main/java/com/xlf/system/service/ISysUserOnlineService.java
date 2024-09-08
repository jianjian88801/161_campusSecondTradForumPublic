package com.xlf.system.service;

import com.xlf.common.security.dao.LoginUser;
import com.xlf.common.security.dao.UserDetailsImpl;
import com.xlf.system.domain.dto.SysUserOnline;

/**
 * 在线用户 服务层
 *
 * @author ruoyi
 */
public interface ISysUserOnlineService
{
    /**
     * 通过登录地址查询信息
     *
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, UserDetailsImpl user);

    /**
     * 通过用户名称查询信息
     *
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByUserName(String userName, UserDetailsImpl user);

    /**
     * 通过登录地址/用户名称查询信息
     *
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, UserDetailsImpl user);

    /**
     * 设置在线用户信息
     *
     * @param user 用户信息
     * @return 在线用户
     */
    public SysUserOnline loginUserToUserOnline(UserDetailsImpl user);
}
