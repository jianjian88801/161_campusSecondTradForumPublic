package com.xlf.auth.strategy.mode;

import com.xlf.common.exception.auth.JwtAuthenticationException;
import com.xlf.common.security.dto.UserDetailsService;
import com.xlf.common.security.dto.SocialTokenDTO;
import com.xlf.common.security.dto.SocialUserInfoDTO;
import com.xlf.auth.strategy.SocialLoginStrategy;
import com.xlf.common.util.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 第三方登录抽象模板
 */
@Service
public abstract class AbstractSocialLoginStrategyImpl implements SocialLoginStrategy {


    /**
     *
     */
    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private Redisson redisson;

    private Boolean isTakeSocialInfo = true;



    /**
     * 是否根据SocialTokenDTO自动注册
     */
    private Boolean isAutoRegist = true;
    /**
     * 主要的任务是根据data从数据库查用户出来（包括用户的权限）
     * 如果找不到该用户
     * @param data 数据
     * @return
     */
    @Override
    public UserDetails login(Object data) {

        // 1. 获取第三方token信息
        SocialTokenDTO socialToken = getSocialToken(data);

        if(StringUtils.isEmpty(socialToken.getOpenid()))
            throw new JwtAuthenticationException("凭证过期，请重新登录!");

        // 2. 从数据库拿用户并且封装权限信息
        UserDetails userDetailDTO = null;
        RLock lock = redisson.getLock("loginLock:" + socialToken.getOpenid());

        //防止同一个用户重复注册
        lock.lock();// 加锁，拿不到锁会一直自旋等待
        //redisson加的锁默认过期时长是30s,没过10s续一次
        //redisson不会死锁，因为有过期时间
        try {

            userDetailDTO = getUser(socialToken.getOpenid());
        } catch (UsernameNotFoundException e) {
//            e.printStackTrace();
            //3.如果用户不存在自动注册
            if (isAutoRegist&&Objects.isNull(userDetailDTO)) {

                SocialUserInfoDTO socialUserInfo = new SocialUserInfoDTO(socialToken.getOpenid(),socialToken.getLoginType());

                // 获取第三方用户信息
                if(isTakeSocialInfo)
                    getSocialUserInfo(socialUserInfo,socialToken);

                //去注册。
                saveUserDetail(socialUserInfo);
                // 再次从数据库拿用户并且封装权限信息
                userDetailDTO = getUser(socialToken.getOpenid());

            }
        } finally {
            //释放
            lock.unlock();
        }


        return userDetailDTO;
    }

    /**
     * 获取第三方token信息
     *
     * @param data 数据
     * @return {@link SocialTokenDTO} 第三方token信息
     */
    public abstract SocialTokenDTO getSocialToken(Object data);

    /**
     * 封装第三方用户信息
     *
     * @param
     * @return {@link SocialUserInfoDTO}
     */
    public abstract void getSocialUserInfo(SocialUserInfoDTO socialUserInfoDTO,SocialTokenDTO socialTokenDTO);


    /**
     * 从数据库拿用户
     * @param token
     * @return
     */
    private UserDetails getUser(String token) {

        UserDetails userDetails = userDetailsService.getUserDetailsByToken(token);

        return userDetails;
    }

    /**
     * 新增用户信息
     *
     * @return {@link UserDetails} 用户信息
     */
    private void saveUserDetail(SocialUserInfoDTO socialUserInfoDTO) {
        userDetailsService.createUser(socialUserInfoDTO);
    }


}
