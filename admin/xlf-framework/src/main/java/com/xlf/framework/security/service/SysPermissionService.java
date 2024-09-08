package com.xlf.framework.security.service;

import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.security.util.SecurityUtils;
import com.xlf.system.mapper.SysRoleMapper;
import com.xlf.system.service.SysMenuService;
import com.xlf.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户权限处理
 *
 * @author ruoyi
 */
@Component
public class SysPermissionService
{
    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysMenuService menuService;

    @Resource
    private SysRoleMapper roleMapper;

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
//    public Set<String> getRolePermission(SysUser user)
//    {
//        Set<String> roles = new HashSet<String>();
//
//        roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
//
//        return roles;
//    }

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user)
    {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (SecurityUtils.isAdmin(user.getUserId()))
        {
            perms.add("*:*:*");
        }
        else
        {
            perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId()));
            //角色名称列表
            user.setRoles(roleMapper.selectRolePermissionByUserId(user.getUserId()).stream().map(item2 -> item2.getRoleName()).collect(Collectors.toList()));
        }
        return perms;
    }

    /**
     * 拿到所有角色权限
     * @param user
     * @return
     */
    public Set<String> getRolePermission(SysUser user) {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (SecurityUtils.isAdmin(user.getUserId()))
        {
            roles.add("admin");
        }
        else
        {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
        }
        return roles;
    }
}
