package com.xlf.system.mapper;

import com.xlf.common.pojo.domain.system.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 小新
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Mapper
* @createDate 2023-02-04 16:35:13
* @Entity com.xlf.common.pojo.domain.system.SysMenu
*/
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户id拿菜单权限
     * @param userId
     * @return
     */
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 拿开启的全部菜单
     * @return
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * 拿开启的路由、菜单
     * @param userId
     * @return
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 通过用户id拿MenuList
     * @param menu
     * @return
     */
    List<SysMenu> selectMenuListByUserId(SysMenu menu);

    /**
     * 通过角色id拿菜单listId
     * @param roleId
     * @param menuCheckStrictly
     * @return
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") Boolean menuCheckStrictly);
}




