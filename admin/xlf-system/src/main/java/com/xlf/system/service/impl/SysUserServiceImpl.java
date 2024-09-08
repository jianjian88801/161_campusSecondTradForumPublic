package com.xlf.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.common.constant.UserConstants;
import com.xlf.common.exception.ServiceException;
import com.xlf.common.pojo.domain.system.*;
import com.xlf.common.security.util.SecurityUtils;
import com.xlf.common.util.BeanCopyUtils;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.page.PageDomain;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.Follow;
import com.xlf.system.domain.Talk;
import com.xlf.system.domain.TalkComment;
import com.xlf.system.domain.UserDetails;
import com.xlf.system.domain.vo.UserFollowInfoVo;
import com.xlf.system.domain.vo.UserVo;
import com.xlf.system.mapper.*;
import com.xlf.system.service.RedisCacheBuilder;
import com.xlf.system.service.SysUserRoleService;
import com.xlf.system.service.SysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.xlf.common.constant.Constants.*;
import static com.xlf.common.util.page.TableSupport.getPageDomain;

/**
* @author 小新
* @description 针对表【sys_user(用户信息表)】的数据库操作Service实现
* @createDate 2023-02-04 16:32:58
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{


    @Resource
    SysRoleMapper roleMapper;

    @Resource
    SysUserMapper userMapper;

    @Resource
    SysUserRoleMapper userRoleMapper;


    @Resource
    SysUserRoleService userRoleService;

    @Resource
    RedisCache redisCache;

    @Resource
    FollowMapper followMapper;

    @Resource
    RedisCacheBuilder redisCacheBuilder;

    @Resource
    UserDetailsMapper userDetailsMapper;

    @Override
    public SysUser selectUserByUserName(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName,username);
        return getOne(wrapper);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName)
    {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }



    @Override
    public boolean resetUserPwd(String userName, String encryptPassword) {

        return update().set("password",encryptPassword).eq("userName",userName).update();

    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhonenumber, user.getPhonenumber()));
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, user.getEmail()));
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUnallocatedList(SysUser user)
    {
        return userMapper.selectUnallocatedList(user);
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user)
    {
        if (StringUtils.isNotNull(user.getUserId()) && user.admin())
        {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId)
    {
        if (!SysUser.admin((Long) SecurityUtils.getUserId()))
        {
            if (getById(userId)==null)
            {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    @Override
    public void insertUserAuth(Long userId, Long[] roleIds) {

        //删除角色和用户关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId,userId));

        insertUserRole(userId, roleIds);
    }

    @Override
    public SysUser selectUserById(Long userId) {
        return getById(userId);
    }

    @Override
    public Boolean updateUserStatus(SysUser user) {
        return lambdaUpdate().set(SysUser::getStatus,user.getStatus()).eq(SysUser::getUserId,user.getUserId()).update();

    }

    @Override
    public Boolean resetPwd(SysUser user) {
        return lambdaUpdate().set(SysUser::getPassword,user.getPassword()).eq(SysUser::getUserId,user.getUserId()).update();
    }

    @Transactional
    @Override
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds)
        {
            checkUserAllowed(SysUser.builder().userId(userId).build());
            checkUserDataScope(userId);
        }
        // 删除用户与角色关联
        userRoleService.remove(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId,userIds));


        return userMapper.deleteBatchIds(Arrays.stream(userIds).collect(Collectors.toList()));
    }

    @Override
    public String checkUserNameUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName,user.getUserName()));
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Transactional
    @Override
    public Boolean updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleService.remove(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId,userId));
        // 新增用户与角色管理
        insertUserRole(user);
        user.setPassword(null);
        return updateById(user);
    }

    @Transactional
    @Override
    public Boolean insertUser(SysUser user) {
        //主键回填
        boolean save = save(user);
         // 新增用户与角色管理
        insertUserRole(user);
        return save;
    }

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    @Override
    public boolean updateUserAvatar(String username, String avatar) {
        return lambdaUpdate().set(SysUser::getAvatar,avatar).eq(SysUser::getUserName,username).update();
    }


    /**
     * 根据id获取用户信息
     * @param userId
     * @return
     */
    @Override
    public SysUser getUserInfoById(Long userId) {

        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();

        userLambdaQueryWrapper.eq(SysUser::getUserId,userId);

        userLambdaQueryWrapper.select(SysUser::getUserId,SysUser::getNickName,SysUser::getSex,SysUser::getAvatar);

        SysUser user = userMapper.selectOne(userLambdaQueryWrapper);
        Optional.ofNullable(user).ifPresent(item->{
            LambdaQueryWrapper<SysUserRole> sysUserRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sysUserRoleLambdaQueryWrapper.eq(SysUserRole::getUserId,item.getUserId()).select(SysUserRole::getRoleId);
            List<Long> collect = userRoleMapper.selectList(sysUserRoleLambdaQueryWrapper).stream().map(item2 -> item2.getRoleId()).collect(Collectors.toList());
            if(StringUtils.isNotEmpty(collect)){
                user.setRoles(roleMapper.selectRolePermissionByUserId(item.getUserId()).stream().map(item2 -> item2.getRoleName()).collect(Collectors.toList()));
            }

        });
        return user;
    }

    /**
     *关注
     * @param userId
     * @param userById 被关注的人
     */
    @Override
    public void follow(Long userId, Long userById) {

        Date date = new Date();
        //我的关注列表
        String key =  getUserFollow(userId.toString());
        redisCache.consumerWithMutexAndBuild(key,(k)->{
            if(redisCache.isZSetMember(k,userById.toString()))
                redisCache.removeZSet(k,userById.toString());
            else
                redisCache.setCacheZSet(k,userById.toString(),date.getTime());
        },(k)->redisCacheBuilder.buildFollowCache(userId,k));

        //对方的粉丝列表
        String key2 =  getUserFans(userById.toString());
        redisCache.consumerWithMutexAndBuild(key2,(k)->{
            if(redisCache.isZSetMember(k,userId.toString()))
                redisCache.removeZSet(k,userId.toString());
            else
                redisCache.setCacheZSet(k,userId.toString(),date.getTime());
        },(k)->redisCacheBuilder.buildFansCache(userById,k));
    }

    /**
     * 获取关注、粉丝等信息
     * @param userId
     * @return
     */
    @Override
    public UserFollowInfoVo getFollowInfo(Long userId) {
        UserFollowInfoVo userFollowInfoVo = new UserFollowInfoVo();
        userFollowInfoVo.setFansNum(getUserFansNum(userId)-1);
        userFollowInfoVo.setFollowNum(getUserFollowNum(userId)-1);
        userFollowInfoVo.setIsFollow(0);
        UserDetails userDetails = userDetailsMapper.selectById(userId);
        if(userDetails!=null)
            userFollowInfoVo.setBarImage(userDetails.getBackground());

        try {
            String s = ((Long) SecurityUtils.getUserId()).toString();
            String key = getUserFollow(s);
            //自己
            if(userId.toString()==s)
                return userFollowInfoVo;
            Boolean isFollow = redisCache.isZSetMember(key, userId.toString());
            if(isFollow)
                  userFollowInfoVo.setIsFollow(1);
        } catch (Exception e) {
          //  e.printStackTrace();
            //没有登录
        }
        return userFollowInfoVo;
    }

    /**
     * 获取关注分页
     * @param userId
     * @return
     */
    @Override
    public List<UserVo> getFollowList(Long userId) {
        String key = getUserFollow(userId.toString());
        //分页信息
        PageDomain pageDomain = getPageDomain();

        Set<String> userIds = redisCache.getCacheZSet(key, pageDomain.getPageNum(), pageDomain.getPageSize());

        ArrayList<SysUser> sysUsers = new ArrayList<>();
        if(StringUtils.isNotEmpty(userIds)){
            userIds.forEach(item->{
                SysUser user = userMapper.getNickNameAndAvatarByUserIdSysUser(Long.parseLong(item));
                if(user!=null)
                    sysUsers.add(user);
            });
        }
        return BeanCopyUtils.copyList(sysUsers, UserVo.class);
    }

    /**
     * 获取粉丝分页
     * @param userId
     * @return
     */
    @Override
    public List<UserVo> getFansList(Long userId) {
        String key = getUserFans(userId.toString());
        //分页信息
        PageDomain pageDomain = getPageDomain();

        Set<String> userIds = redisCache.getCacheZSet(key, pageDomain.getPageNum(), pageDomain.getPageSize());

        ArrayList<SysUser> sysUsers = new ArrayList<>();
        if(StringUtils.isNotEmpty(userIds)){
            userIds.forEach(item->{
                SysUser user = userMapper.getNickNameAndAvatarByUserIdSysUser(Long.parseLong(item));
                if(user!=null)
                    sysUsers.add(user);
            });
        }
        return BeanCopyUtils.copyList(sysUsers, UserVo.class);
    }



    /**
     * 获取用户的关注的数量
     * @param userId
     * @return
     */
    private Integer getUserFollowNum(Long userId) {
        String key = getUserFollow(userId.toString());
        return redisCache.queryWithMutexAndBuild(key,(k)->redisCache.getZSetSize(k).intValue(),(k)->{
            redisCacheBuilder.buildFollowCache(userId,k);
           return redisCache.getZSetSize(k).intValue();
        });
    }

    /**
     * 获取用户的粉丝的数量
     * @param userId
     * @return
     */
    private Integer getUserFansNum(Long userId) {
        String key = getUserFans(userId.toString());
        return redisCache.queryWithMutexAndBuild(key,(k)->redisCache.getZSetSize(k).intValue(),(k)->{
            redisCacheBuilder.buildFansCache(userId,k);
            return redisCache.getZSetSize(k).intValue();
        });
    }

    /**
     * 构建关注缓存
     * @param userId
     * @param key
     */
    @Deprecated
    private void buildFollowCache(Long userId, String key) {
        Set<String> follows = followMapper.selectList(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUserId, userId)
                .select(Follow::getUserById,Follow::getCreateTime)).stream().map(item->item.getUserById().toString()).collect(Collectors.toSet());
        redisCache.setCacheSet(key,follows);
    }

    /**
     * 构建粉丝缓存
     * @param userById
     * @param key
     */
    @Deprecated
    private void buildFansCache(Long userById, String key) {
        Set<String> follows = followMapper.selectList(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUserById, userById)
                .select(Follow::getUserId)).stream().map(item->item.getUserId().toString()).collect(Collectors.toSet());
        redisCache.setCacheSet(key,follows);
    }


    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user)
    {
        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 新增用户角色信息
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds)
    {
        if (StringUtils.isNotEmpty(roleIds))
        {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length);
            for (Long roleId : roleIds)
            {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleService.saveBatch(list);
        }
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectAllocatedList(SysUser user)
    {
        return userMapper.selectAllocatedList(user);
    }




}




