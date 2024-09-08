package com.xlf.framework.dao;

import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.security.dao.UserDetailsImpl;
import com.xlf.common.security.dto.SocialUserInfoDTO;
import com.xlf.common.security.dto.UserDetailsService;
import com.xlf.common.util.StringUtils;
import com.xlf.framework.security.service.SysPermissionService;
import com.xlf.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private SysUserService userService;


    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private com.xlf.system.service.UserDetailsService userDetailsService;


    @Override
    public UserDetails getUserDetailsByToken(String token) {
        SysUser user = userService.selectUserByUserName(token);

        if (StringUtils.isNull(user))
        {
            throw new UsernameNotFoundException("用户：" + token + " 不存在");
        }

        return createJwtUserDetails(user);
    }

    public UserDetails createJwtUserDetails(SysUser user)
    {
        return new UserDetailsImpl(user, permissionService.getMenuPermission(user));
    }

    @Override
    public void createUser(SocialUserInfoDTO registerInfo) {
        SysUser sysUser = new SysUser();
        sysUser.setNickName(registerInfo.getNickname());
        sysUser.setUserName(registerInfo.getUsername());
        sysUser.setPassword(registerInfo.getPassword());
        sysUser.setAvatar(registerInfo.getAvatar());
        sysUser.setDelFlag("0");
        sysUser.setStatus("0");
        sysUser.setCreateTime(new Date());
        if(registerInfo.getType()!=null)
            sysUser.setUserType(registerInfo.getType().toString());
        Long [] roles = {6L};
        sysUser.setRoleIds(roles);
        userService.insertUser(sysUser);

    }
}
