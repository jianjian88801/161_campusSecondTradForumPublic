package com.xlf.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.pojo.mode.BaseEntity;
import com.xlf.system.domain.vo.UserVo;
import lombok.Data;

/**
 *
 * @TableName talk
 */
@TableName(value ="talk")
@Data
public class Talk extends BaseEntity implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 话题id
     */
    private Long typeId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 发布内容
     */
    private String content;


    /**
     *
     */
    private Integer delFlag;

    /**
     * 评论数
     */
    @TableField(exist = false)
    private Integer commentNum;

    /**
     * 回复的评论
     */
    @TableField(exist = false)
    private Page<TalkComment> comment;

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

    /**
     * 用户是否收藏
     */
    @TableField(exist = false)
    private Integer star;

    /**
     * 点赞量
     */
    @TableField(exist = false)
    private Integer ThumbsNum;

    /**
     * 用户点赞列表
     */
    @TableField(exist = false)
    private List<UserVo> thumbsList;

    /**
     * 距离当前时间的长度
     */
    @TableField(exist = false)
    private String date;

    @TableField(exist = false)
    private String typeName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
