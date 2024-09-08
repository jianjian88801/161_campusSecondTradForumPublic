package com.xlf.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.xlf.common.pojo.mode.BaseEntity;
import lombok.Data;

/**
 *
 * @TableName user_details
 */
@TableName(value ="user_details")
@Data
public class UserDetails implements Serializable {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 主页背景
     */
    private String background;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
