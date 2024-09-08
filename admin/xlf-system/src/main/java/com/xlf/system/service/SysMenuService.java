package com.xlf.system.service;

import com.xlf.common.pojo.domain.system.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xlf.common.pojo.dto.TreeSelect;
import com.xlf.system.domain.vo.RouterVo;

import java.util.List;
import java.util.Set;

/**
* @author 小新
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service
* @createDate 2023-02-04 16:35:13
*/
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByUserId(Long userId);

    List<SysMenu> selectMenuTreeByUserId(Long userId);

    List<RouterVo> buildMenus(List<SysMenu> menus);

    List<SysMenu> selectMenuList(SysMenu menu, Long userId);
    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(Long userId);

    SysMenu selectMenuById(Long menuId);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    public List<Long> selectMenuListByRoleId(Long roleId);

    String checkMenuNameUnique(SysMenu menu);

    boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkMenuExistRole(Long menuId);
}
