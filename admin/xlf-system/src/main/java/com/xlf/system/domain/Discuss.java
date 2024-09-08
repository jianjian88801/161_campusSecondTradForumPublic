package com.xlf.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.pojo.mode.BaseEntity;
import lombok.Data;

/**
 *
 * @TableName discuss
 */
@TableName(value ="discuss")
@Data
public class Discuss extends BaseEntity implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 话题id
     */
    private Long topicId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 发布内容
     */
    private String content;


    private Integer delFlag;


    /**
     * 点赞数
     */
    @TableField(exist = false)
    private Integer thumbsNum;


    /**
     * 评论数
     */
    @TableField(exist = false)
    private Integer commentNum;

    /**
     * 回复的评论
     */
    @TableField(exist = false)
    private List<Comment> comment;

    /**
     * 发布的用户
     */
    @TableField(exist = false)
    private SysUser user;

    /**
     * 图片列表
     */
    @TableField(exist = false)
    private List<String>pictrueList;

    /**
     * 用户是否点赞
     */
    @TableField(exist = false)
    private Integer thumbs;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
