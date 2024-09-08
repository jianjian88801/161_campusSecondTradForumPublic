package com.xlf.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @TableName talk_thumbs
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="talk_thumbs")
@Data
public class TalkThumbs  implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 话题id
     */
    private Long talkId;

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
