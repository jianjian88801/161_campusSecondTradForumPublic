package com.xlf.system.mq.builder;


import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


/**
 * author: xlf
 */
public abstract class AbstractTemplateBuilder {


    protected JSONObject product = new JSONObject();


    /**
     *跳转小程序类型：developer为开发版；trial为体验版；formal为正式版；默认为正式版
     */
    public void buildMiniprogramState(){
        product.put("miniprogram_state","developer");
    }

    /**
     * 进入小程序查看的语言类型，支持zh_CN(简体中文)、en_US(英文)、zh_HK(繁体中文)、zh_TW(繁体中文)，默认为zh_CN
     */
    public void buildLang(){
        product.put("lang","zh_CN");
    }

    /**
     * 模板id
     */
    public abstract void buildTemplateId();

    /**
     * 订阅的人
     */
    public abstract void buildOpenId();

    /**
     * 跳转的页面
     */
    public abstract void buildPage();


    /**
     * 模板消息
     */
    public abstract void buildData();

    /**
     * 获取模板
     * @return
     */
    public JSONObject construct(){
        buildTemplateId();
        buildPage();
        buildOpenId();
        buildData();
        buildMiniprogramState();
        buildLang();
        return product;
    }

    /**
     * data构建者
     * @return
     */
    public DataBuilder DataBuilder(){
        DataBuilder dataBuilder = new DataBuilder();
        return dataBuilder;
    }

    /**
     *
     */
    protected class DataBuilder {

        private JSONObject data;

        public DataBuilder(){
            data = new JSONObject();
        }

        public DataBuilder addParam(String key, String value){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value",value);
            data.put(key,jsonObject);
            return this;
        }

        public JSONObject build(){
            return data;
        }
    }




}
