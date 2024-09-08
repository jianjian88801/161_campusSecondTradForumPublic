package com.xlf.system.mq.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.xlf.system.domain.TalkComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TalkCommentDTO implements Serializable {

    /**
     * 唯一标识
     */
    private Long id;

    private Long talkId;

    /**
     * 订阅的人的openId
     */
    private Set<String> openIds;
    private String touser;

    /**
     * 帖子内容
     */
    private String talkContent;

    /**
     * 回复的人名字
     */
    private String nickName;

    /**
     * 回复的内容
     */
    private String content;

    /**
     * 回复的时间
     */
    private String data;




}
