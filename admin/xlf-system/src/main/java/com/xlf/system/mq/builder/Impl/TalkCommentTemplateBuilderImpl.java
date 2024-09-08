package com.xlf.system.mq.builder.Impl;


import com.alibaba.fastjson2.JSONObject;
import com.xlf.system.domain.Talk;
import com.xlf.system.domain.TalkComment;
import com.xlf.system.mq.builder.AbstractTemplateBuilder;
import com.xlf.system.mq.dto.TalkCommentDTO;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author: xlf
 */
public class TalkCommentTemplateBuilderImpl extends AbstractTemplateBuilder {


    private TalkCommentDTO talkComment;

    public TalkCommentTemplateBuilderImpl(TalkCommentDTO talkComment){
        super();
        this.talkComment = talkComment;
    }

    /**
     * 订阅模板
     */
    @Override
    public void buildTemplateId() {
        product.put("template_id","w6J_8bSEtK_2Tnscws-HWKJ_SxG0ANCQIcOZwK-AsWw");
    }

    /**
     * 订阅的人
     */
    @Override
    public void buildOpenId() {
        product.put("touser",talkComment.getTouser());
    }

    /**
     * 点击订阅消息跳转的页面
     */
    @Override
    public void buildPage() {
        product.put("page","/pages/chatDetail/chatDetail?id="+talkComment.getTalkId());
    }

    /**
     * 模板消息
     */
    @Override
    public void buildData() {
        JSONObject data = DataBuilder()
                .addParam("thing1", talkComment.getTalkContent()) //帖子内容
                .addParam("thing4", talkComment.getNickName()) //评论用户
                .addParam("thing2", talkComment.getContent()) //评论内容
                .addParam("time3", talkComment.getData()) //评论的时间
                .build();
        product.put("data",data);
    }






}
