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
 * @TableName type
 */
@TableName(value ="type")
@Data
public class Type extends BaseEntity implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 帖子数目
     */
    @TableField(exist = false)
    private Integer num;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
