package com.xlf;

import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.security.dao.JwtUserDetails;
import com.xlf.common.util.file.QinIuUtils;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.common.util.spring.SpringUtils;
import com.xlf.system.domain.Discuss;
import com.xlf.system.domain.Talk;
import com.xlf.system.domain.TalkComment;
import com.xlf.system.mapper.SysUserMapper;
import com.xlf.system.mq.producer.TalkProducer;
import com.xlf.system.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.web.DefaultSecurityFilterChain;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.xlf.common.constant.TalkConstants.getTalkThumbs;

@SpringBootTest
class XlfInterfaceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Resource
    DiscussService discussService;

    @Resource
    RedisCache redisCache;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    QinIuUtils qinIuUtils;

    @Resource
    TalkProducer talkProducer;

    @Resource
    TalkService talkService;

    @Resource
    RedisCacheBuilder redisCacheBuilder;

    @Resource
    TalkCommentService talkCommentService;

    @Test
    void discussTest() {

        DefaultSecurityFilterChain bean = SpringUtils.getBean(DefaultSecurityFilterChain.class);

//
//        List<Discuss> list = discussService.getDisCussListByTopicId(1, 1L);
//
//        System.out.println(list);

//        JwtUserDetails abc = redisCache.getCacheObject("login_tokens:9abb78e2-16a7-4f30-abbc-26853403dc6c");
//        System.out.println(abc);

//        redisCache.setCacheObject("test",1L);
//        Long test = redisCache.getCacheObject("test");
//        System.out.println(test);

      //  redisCache.setCacheMapValue("tt","t",1L);

    //    Long cacheMapValue = redisCache.getCacheMapValue("tt", "t");

   //     long l = redisCache.mapIncrement("tt", "t", 1L);


//        Set<Long> cacheSet = redisCache.getCacheSet("topic:thumbs:1636348289433493505");
//
//        cacheSet.forEach(item->{
//            System.out.println(item);
//        });
//
//        SysUser user = new SysUser();
//        user.setUserId(10L);
//        user.setNickName("111");
//        user.setUserName("121212121212");
//        sysUserMapper.insert(user);

//        Set<ZSetOperations.TypedTuple<String>> cacheZSet = redisCache.getCacheZSetWithScore("user:follow:7");
//        cacheZSet.forEach(item->{
//            System.out.println(item.getValue());
//            System.out.println(item.getScore());
//
//        });
//
//        String token = qinIuUtils.getToken();
//        System.out.println(token);
//        TalkComment talkComment = new TalkComment();
//        talkComment.setTalkId(22L);
//        talkComment.setCommentBy("1");
//        talkComment.setDate("123");
//        SysUser user = new SysUser();
//        user.setNickName("呵呵呵呵");
//        user.setUserName("oEnLO5bJlfjhPLUWKkClYuP57i2o");
//        talkComment.setUser(user);
//        talkComment.setUserBy(user);
//        talkComment.setContent("hhhh");
//        talkComment.setId(111L);
//
//        talkComment.setCreateTime(new Date());
//        talkProducer.sendMsg(talkComment);
//        for (int i = 0; i < 10; i++) {
//            Thread thread = new Thread(() -> {
//                for(int j=1;j<=1000000;j++){
//                    Talk talk = new Talk();
//                    talk.setCreateTime(new Date());
//                    talk.setContent("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
//                    talk.setUserId(1L);
//                    talkService.addTalk(talk,1L);
//                }
//            });
//            thread.run();
//        }

        Date date = new Date();
        System.out.println(date);
        System.out.println(date.getTime());
        System.out.println(System.currentTimeMillis());


    }


    @Test
    public void test100(){
        List<Talk> list = talkService.list();
        list.forEach(item->{

        });
    }


    @Test
    void thumbs(){

        List<SysUser> list = sysUserMapper.selectList(null);
        Long talkId = 1L;
        list.forEach(item->{
            String userId = item.getUserId().toString();
            String key =  getTalkThumbs(talkId.toString());
            redisCache.consumerWithMutexAndBuild(key,(k)->{
                if(redisCache.isZSetMember(k,userId))
                    redisCache.removeZSet(k,userId);
                else
                    redisCache.setCacheZSet(k,userId,System.currentTimeMillis());
            },(k)->redisCacheBuilder.buildTalkThumbCache(talkId,k));
        });
    }



}
