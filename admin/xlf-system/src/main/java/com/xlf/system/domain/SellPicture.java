package com.xlf.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName sell_picture
 */
@TableName(value ="sell_picture")
@Data
public class SellPicture  implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private Long sellId;

    /**
     *
     */
    private String url;

    /**
     *
     */
    private String path;

    private String width;

    private String height;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
