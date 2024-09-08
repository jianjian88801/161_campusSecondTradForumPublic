package com.xlf.system.mq.builder;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xlf.common.properties.WxConfigProperties;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.TalkComment;
import com.xlf.system.mq.builder.Impl.TalkCommentTemplateBuilderImpl;
import com.xlf.system.mq.dto.TalkCommentDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.xlf.common.constant.RedisConst.TALK_COMMENT_SUBSCRIPTION;

@Slf4j
@Service
public class SubscribeService {

    @Autowired
    WxConfigProperties wxConfigProperties;

    @Autowired
    RedisCache redisCache;

    private final String ASSESS_TOKEN_CACHE = "access_token_cache";
    /*
    获取token的url
     */
    private String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=";
    /*
    发订阅内容的url
     */
    private  String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=";

    private Map<String,Object> params;

    /**
     *
     * @return
     */
    public void SendTalkCommentSubscriptionMessage(TalkCommentDTO talkComment){

        String access_token = getAccessToken();
        log.info("token:{}",access_token);
        Set<String> openIds = talkComment.getOpenIds();
        if(StringUtils.isNotEmpty(openIds)){
            openIds.forEach(item->{
                talkComment.setTouser(item);
                AbstractTemplateBuilder talkCommentTemplateBuilder = new TalkCommentTemplateBuilderImpl(talkComment);
                JSONObject product = talkCommentTemplateBuilder.construct();
                sead(access_token,product);
            });
        }

    }

    /**
     * 获取AccessToken
     * @return
     */
    private String getAccessToken() {
        String url  = tokenUrl  + wxConfigProperties.getAppId() + "&secret=" + wxConfigProperties.getAppSecret();

//        access_token 的有效期通过返回的 expires_in 来传达，目前是7200秒之内的值，中控服务器需要根据这个有效时间提前去刷新。
//        在刷新过程中，中控服务器可对外继续输出的老 access_token，此时公众平台后台会保证在5分钟内，新老 access_token 都可用，这保证了第三方业务的平滑过渡
        return redisCache.queryWithMutexAndBuild(ASSESS_TOKEN_CACHE, (k) -> redisCache.getCacheObject(k), (k) -> {
            AccessToken accessToken = JSON.parseObject(HttpUtil.get(url), AccessToken.class);
            log.info("{} + accessToken:{}",url,accessToken);
            redisCache.setCacheObject(k, accessToken.getAccess_token(), accessToken.getExpires_in(), TimeUnit.SECONDS);
            return accessToken.getAccess_token();
        });
    }


    /**
     *推送
     * @return
     */
    public void sead(String access_token,JSONObject params){

        String res = HttpUtil.post(url + access_token,params.toJSONString());

        System.out.println(params.toString());

        SendRes sendRes = JSON.parseObject(res, SendRes.class);

        log.info("成功发送：{}",sendRes);
        switch (sendRes.errcode){
            case 40001:
                throw new RuntimeException("获取 access_token 时 AppSecret 错误，或者 access_token 无效。请开发者认真比对 AppSecret 的正确性，或查看是否正在为恰当的公众号调用接口");
            case 40003:
                throw new RuntimeException("不合法的 OpenID ，请开发者确认 OpenID （该用户）是否已关注公众号，或是否是其他公众号的 OpenID");
            case 40014:
                throw new RuntimeException("不合法的 access_token ，请开发者认真比对 access_token 的有效性（如是否过期），或查看是否正在为恰当的公众号调用接口");
            case 40037:
                throw new RuntimeException("不合法的 template_id");

        }
    }

    @Data
    private class SendRes implements Serializable{

        private Integer errcode;

        private String errmsg;
    }

    @Data
    public class AccessToken implements Serializable {

        private String access_token;
        private int expires_in;
    }



}
