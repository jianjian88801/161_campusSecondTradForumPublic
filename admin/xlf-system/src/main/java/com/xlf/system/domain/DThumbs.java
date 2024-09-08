package com.xlf.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.xlf.common.pojo.mode.BaseEntity;
import lombok.Data;

/**
 *
 * @TableName d_thumbs
 */
@TableName(value ="d_thumbs")
@Data
public class DThumbs extends BaseEntity implements Serializable {
    /**
     * 话题id
     */
    @TableId(type = IdType.AUTO)
    private Long discussId;

    /**
     * 点赞用户
     */
    private Long userId;

    /**
     * 点赞时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
