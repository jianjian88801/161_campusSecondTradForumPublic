package com.xlf.system.domain.vo;

import com.xlf.common.pojo.domain.system.SysUser;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {


    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 性别
     */
    private String sex;
}
