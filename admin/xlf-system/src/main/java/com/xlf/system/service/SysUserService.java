package com.xlf.system.service;

import com.xlf.common.pojo.domain.system.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xlf.system.domain.TalkComment;
import com.xlf.system.domain.vo.UserFollowInfoVo;
import com.xlf.system.domain.vo.UserVo;

import java.util.List;

/**
* @author 小新
* @description 针对表【sys_user(用户信息表)】的数据库操作Service
* @createDate 2023-02-04 16:32:58
*/
public interface SysUserService extends IService<SysUser> {

    /**
     * 通过用户名获取用户
     * @param username
     * @return
     */
    SysUser selectUserByUserName(String username);
    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserRoleGroup(String userName);



    boolean resetUserPwd(String userName, String encryptPassword);

    String checkPhoneUnique(SysUser user);

    String checkEmailUnique(SysUser user);


    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUnallocatedList(SysUser user);


    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    public void checkUserAllowed(SysUser user);

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    public void checkUserDataScope(Long userId);

    void insertUserAuth(Long userId, Long[] roleIds);

    SysUser selectUserById(Long userId);

    Boolean updateUserStatus(SysUser user);


    Boolean resetPwd(SysUser user);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    int deleteUserByIds(Long[] userIds);

    String checkUserNameUnique(SysUser user);

    Boolean updateUser(SysUser user);

    Boolean insertUser(SysUser user);

    List<SysUser> selectUserList(SysUser user);

    boolean updateUserAvatar(String username, String avatar);

    SysUser getUserInfoById(Long userId);

    void follow(Long userId, Long userById);

    UserFollowInfoVo getFollowInfo(Long userId);

    List<UserVo> getFollowList(Long userId);

    List<UserVo> getFansList(Long userId);

}
