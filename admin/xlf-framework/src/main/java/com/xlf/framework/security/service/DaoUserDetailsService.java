package com.xlf.framework.security.service;

import com.xlf.common.constant.UserStatus;
import com.xlf.common.exception.auth.DaoAuthUserDetailsException;
import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.security.dao.UserDetailsImpl;
import com.xlf.common.util.StringUtils;
import com.xlf.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 默认登录的业务实现。任务是根据username从数据库拿出用户。然后判断用户状态。
 *
 * 返回值为抽象JwtUserDetails的实现类
 * 实际上判断用户状态等认证在AbstractUserDetailsAuthenticationProvider已经可以帮我们做，只需要设置UserDetails的几个属性，但是tm抛的全是英文。
 *
 *
 */
@Service
public class DaoUserDetailsService implements UserDetailsService {

    @Autowired
    private SysUserService userService;


    @Autowired
    private SysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        SysUser user = userService.selectUserByUserName(username);

        if (StringUtils.isNull(user))
        {
            throw new UsernameNotFoundException("用户：" + username + " 不存在");
        }

        return createJwtUserDetails(user);
    }

    public UserDetails createJwtUserDetails(SysUser user)
    {
        return new UserDetailsImpl(user, permissionService.getMenuPermission(user));
    }
}
