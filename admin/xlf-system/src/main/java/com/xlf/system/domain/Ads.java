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
 * @TableName ads
 */
@TableName(value ="ads")
@Data
public class Ads extends BaseEntity implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *跳转路径
     */
    private String path;

    /**
     *
     */
    private String url;

    /**
     *
     */
    private String sort;

    /**
     *
     */
    private Integer status;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
