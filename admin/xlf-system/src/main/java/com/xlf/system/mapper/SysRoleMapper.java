package com.xlf.system.mapper;

import com.xlf.common.pojo.domain.system.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 小新
* @description 针对表【sys_role(角色信息表)】的数据库操作Mapper
* @createDate 2023-02-04 16:35:52
* @Entity com.xlf.common.pojo.domain.system.SysRole
*/
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<SysRole> selectRolePermissionByUserId(Long userId);


    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    public List<Long> selectRoleListByUserId(Long userId);


    /**
     * 根据用户名查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    public List<SysRole> selectRolesByUserName(String userName);

}




