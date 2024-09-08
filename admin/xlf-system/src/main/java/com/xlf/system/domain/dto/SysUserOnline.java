package com.xlf.system.domain.dto;

import lombok.Data;

/**
 * 当前在线会话
 *
 */
@Data
public class SysUserOnline
{
    /** 会话编号 */
    private String tokenId;

    /** 用户名称 */
    private String userName;

    /** 用户昵称 */
    private String nickName;

    /** 用户头像 */
    private String avatar;

    /** 登录IP地址 */
    private String ipaddr;

    /** 登录地址 */
    private String loginLocation;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 登录时间 */
    private Long loginTime;


}
