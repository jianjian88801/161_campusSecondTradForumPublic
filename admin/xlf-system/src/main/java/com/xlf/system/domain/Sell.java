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
import com.xlf.system.domain.vo.SellPictureVo;
import lombok.Data;

/**
 *
 * @TableName sell
 */
@TableName(value ="sell")
@Data
public class Sell extends BaseEntity implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 简介
     */
    private String content;

    /**
     * 联系方式
     */
    private String contact;

    /**
     *
     */
    private Long userId;


    /**
     *
     */
    private Integer delFlag;


    /**
     *
     */
    private String status;

    /**
     *
     */
    private Long userById;


    @TableField(exist = false)
    private String Date;

    /**
     * 发表人
     */
    @TableField(exist = false)
    private SysUser sysUser;

    /**
     * 发表人
     */
    @TableField(exist = false)
    private SysUser sysUserBy;

    @TableField(exist = false)
    private List<SellPictureVo> sellPictures;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
