package com.xlf.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.pojo.mode.BaseEntity;
import com.xlf.system.domain.vo.UserVo;
import lombok.Data;

/**
 *
 * @TableName talk_comment
 */
@TableName(value ="talk_comment")
@Data
public class TalkComment extends BaseEntity implements Serializable {
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
     * 回复的talkId
     */
    private Long talkId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 被回复的Comment
     */
    private Long parentId;


    /**
     *是否删除
     */
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
    private String commentBy;

    /**
     * 距离当前时间的长度
     */
    @TableField(exist = false)
    private String date;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
