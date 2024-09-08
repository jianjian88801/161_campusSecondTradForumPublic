package com.xlf.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.pojo.mode.BaseEntity;
import lombok.Data;

/**
 *
 * @TableName comment
 */
@TableName(value ="comment")
@Data
public class Comment extends BaseEntity implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 回复的人
     */
    private Long userId;

    /**
     * 讨论Id
     */
    private Long discussId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 被回复的人
     */
    private Long parentId;


    private Integer delFlag;

    /**
     * 回复的人
     */
    @TableField(exist = false)
    private SysUser user;

    /**
     * 被回复的人
     */
    @TableField(exist = false)
    private SysUser userBy;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
