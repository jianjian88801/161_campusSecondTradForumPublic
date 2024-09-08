package com.xlf.system.domain.vo;

import lombok.Data;

@Data
public class UserFollowInfoVo {

    /**
     * 关注数量
     */
    private Integer followNum;

    /**
     * 访客数量
     */
    private Integer viewNum;

    /**
     * 粉丝数量
     */
    private Integer fansNum;

    /**
     * 是否已经关注该用户
     */
    private Integer isFollow;

    /**
     * 背景
     */
    private String barImage;

}
