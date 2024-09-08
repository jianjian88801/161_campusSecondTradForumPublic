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
 * @TableName topic
 */
@TableName(value ="topic")
@Data
public class Topic extends BaseEntity implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 话题名称
     */
    private String title;

    /**
     * 话题内容
     */
    private String content;

    /**
     *
     */
    private String picture;

    /**
     * 发表的用户
     */
    private Long userId;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 访问量
     */
    private Integer view;


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
    private Integer discussNum;


    /**
     * 用户是否点赞
     */
    @TableField(exist = false)
    private Integer thumbs;



    @TableField(exist = false)
    private SysUser sysUser;

    /**
     * 用户是否收藏
     */
    @TableField(exist = false)
    private Integer collection;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
