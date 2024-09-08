package com.xlf.auth.strategy.impl;

import cn.hutool.http.HttpUtil;

import com.alibaba.fastjson2.JSON;
import com.xlf.auth.Enum.LoginTypeEnum;
import com.xlf.common.security.dto.SocialTokenDTO;
import com.xlf.common.security.dto.SocialUserInfoDTO;
import com.xlf.common.security.dto.WxLoginDTO;
import com.xlf.common.properties.WxConfigProperties;
import com.xlf.auth.strategy.mode.AbstractSocialLoginStrategyImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("wxLoginStrategyImpl")
public class WxLoginStrategyImpl extends AbstractSocialLoginStrategyImpl {


    @Autowired
    WxConfigProperties wxConfigProperties;

    /**
     * 获取用户唯一标识
     * @param data 数据
     * @return
     */
    @Override
    public SocialTokenDTO getSocialToken(Object data) {

        WxLoginDTO wxLoginDTO = new WxLoginDTO();
        wxLoginDTO.setCode((String) data);
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("appid", wxConfigProperties.getAppId());
        paramMap.put("secret", wxConfigProperties.getAppSecret());
        paramMap.put("js_code", wxLoginDTO.getCode());
        paramMap.put("grant_type","authorization_code");
        String result= null;
        SocialTokenDTO socialTokenDTO = null;

        //result返回请求体
        result = HttpUtil.get(wxConfigProperties.getUrl(), paramMap);
        socialTokenDTO = JSON.parseObject(result, SocialTokenDTO.class);

        socialTokenDTO.setLoginType(LoginTypeEnum.WX.getType());

        return socialTokenDTO;
    }

    /**
     * 获取用户信息
     * @param socialUserInfoDTO
     * @param socialTokenDTO
     */
    @Override
    public void getSocialUserInfo(SocialUserInfoDTO socialUserInfoDTO,SocialTokenDTO socialTokenDTO) {
        socialUserInfoDTO.setNickname("微信用户");
        socialUserInfoDTO.setPassword("123456");
//        socialUserInfoDTO.setAvatar("http://localhost:8088/foodPricutre/f80a0659b31ede626eac3f627a9bb40f.jpg");

    }
}
