package com.xlf.system.mapper;

import com.xlf.common.pojo.domain.system.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 小新
* @description 针对表【sys_user(用户信息表)】的数据库操作Mapper
* @createDate 2023-02-04 16:32:58
* @Entity com.xlf.common.pojo.domain.system.SysUser
*/
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * 根据条件分页查询已配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
     List<SysUser> selectUserList(SysUser sysUser);


    /**
     * 根据用户id拿用户名，头像,性别
     * @param userId
     * @return
     */
    @Select("select user_id,nick_name,avatar,sex from sys_user where user_id = #{userId}")
    SysUser getNickNameAndAvatarByUserIdSysUser(Long userId);

}




