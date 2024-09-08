package com.xlf.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName talk_picture
 */
@TableName(value ="talk_picture")
@Data
public class TalkPicture  implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private Long talkId;

    /**
     *
     */
    private String path;

    /**
     *
     */
    private String url;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
