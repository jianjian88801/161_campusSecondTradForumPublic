package com.xlf.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 *
 * @TableName follow
 */
@TableName(value ="follow")
@Data
public class Follow  implements Serializable {
    /**
     *
     */
    private Long userId;

    /**
     *
     */
    private Long userById;


    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
