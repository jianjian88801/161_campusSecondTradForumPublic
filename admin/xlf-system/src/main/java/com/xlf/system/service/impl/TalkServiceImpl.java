package com.xlf.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.common.constant.HttpStatus;
import com.xlf.common.pojo.domain.system.SysUser;
import com.xlf.common.security.util.SecurityUtils;
import com.xlf.common.util.BeanCopyUtils;
import com.xlf.common.util.StringUtils;
import com.xlf.common.util.page.PageDomain;
import com.xlf.common.util.page.PageResult;
import com.xlf.common.util.page.TableDataInfo;
import com.xlf.common.util.page.TableSupport;
import com.xlf.common.util.redis.RedisCache;
import com.xlf.system.domain.*;
import com.xlf.system.domain.vo.UserVo;
import com.xlf.system.mapper.*;
import com.xlf.system.mq.producer.TalkProducer;
import com.xlf.system.service.RedisCacheBuilder;
import com.xlf.system.service.TalkCommentService;
import com.xlf.system.service.TalkService;
import com.xlf.system.service.TalkThumbsService;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.xlf.common.constant.Constants.*;
import static com.xlf.common.constant.DiscussConstants.DISCUSS_THUMBS;
import static com.xlf.common.constant.TalkConstants.*;
import static com.xlf.common.constant.TopicConstants.TOPIC_DISCUSS_NUM;
import static com.xlf.common.constant.TopicConstants.TOPIC_THUMBS;
import static com.xlf.common.util.date.TimeAgoUtils.format;

/**
* @author 小新
* @description 针对表【talk】的数据库操作Service实现
* @createDate 2023-04-11 19:30:20
*/
@Service
public class TalkServiceImpl extends ServiceImpl<TalkMapper, Talk>
    implements TalkService{

    @Resource
    TalkProducer talkProducer;

    @Resource
    TalkCommentMapper talkCommentMapper;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    TalkPictureMapper talkPictureMapper;

    @Resource
    RedisCache redisCache;

    @Resource
    TalkThumbsMapper talkThumbsMapper;

    @Resource
    UserDetailsMapper userDetailsMapper;

    @Resource
    TypeMapper typeMapper;

    @Resource
    RedisCacheBuilder redisCacheBuilder;


    @Override
    public List<Talk> getPage(Long talkId,Long typeId,Long userId, String content) {

        LambdaQueryWrapper<Talk> wrapper = new LambdaQueryWrapper<Talk>();

        //根据用户查
        wrapper.eq(userId!=null,Talk::getUserId,userId);
        //根据类型查
        wrapper.eq(typeId!=null,Talk::getTypeId,typeId);


        //根据关键字
        wrapper.like(StringUtils.isNotEmpty(content),Talk::getContent,content);

        //未删除
        wrapper.eq(Talk::getDelFlag,0);

        if(talkId==null)
            talkId = Long.MAX_VALUE;
        //分页
        wrapper.lt(Talk::getId,talkId);
        //根据Id降序排序
        wrapper.orderByDesc(Talk::getId);

        //查出5条数据
        wrapper.last("limit 5");

        //查出列表
        List<Talk> list = list(wrapper);
        //列表为空
        if(StringUtils.isEmpty(list))
            return new ArrayList<>();

        setTalkInfo(list);

        return list;
    }



    /**
     *
     * @return
     */
    @Override
    public List<Talk> getPage(Talk talk) {

        LambdaQueryWrapper<Talk> wrapper = new LambdaQueryWrapper<Talk>();
        wrapper.eq(talk.getDelFlag()!=null,Talk::getDelFlag,talk.getDelFlag());
        wrapper.like(StringUtils.isNotEmpty(talk.getContent()),Talk::getContent,talk.getContent());
        if(talk.getParams().containsKey("beginTime")&&talk.getParams().containsKey("endTime"))
             wrapper.between(Talk::getCreateTime,talk.getParams().get("beginTime"),talk.getParams().get("endTime"));
        wrapper.eq(talk.getUserId()!=null,Talk::getUserId,talk.getUserId());
        wrapper.orderByDesc(Talk::getCreateTime);

        List<Talk> list = list(wrapper);
        if(StringUtils.isNotEmpty(list)){
            list.forEach(item->{

                //封装用户
                setUserVo(item);
                //设置分类
                setType(item);

                //设置图片
                setTalkPicture(item);
                //封装点赞量
                setTalkThumbNum(item);
            });
        }
        return list;
    }

    /**
     *
     * @param time 上一次的时间戳
     * @param offSet 偏移量
     * @param pageSize 一次获取多少
     * @return
     */
    @Override
    public PageResult getFollowTalk(Long time, Integer offSet, Integer pageSize) {

        PageResult tableDataInfo = new PageResult();
        ArrayList<Talk> talks = new ArrayList<>();
        tableDataInfo.setRows(talks);
        tableDataInfo.setIsLastPage(0);

        // 1.获取当前用户
        Long userId = (Long) SecurityUtils.getUserId();
        // 2.查询收件箱
        String key = getUserDropBox(userId.toString());

        while (pageSize>0){

            long minTime = 0;
            int os = 1;
            //拿一条数据
            Set<ZSetOperations.TypedTuple<String>> typedTuples =  redisCache.getCacheZSetByScoreWithScores(key, time , offSet , pageSize);
            //没有了，表示已经没有数据了
            if (StringUtils.isEmpty(typedTuples)) {
                tableDataInfo.setIsLastPage(1);
                break;
            }

            // 解析数据
            List<Long> ids = new ArrayList<>();
            for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
                // 4.1.获取id
                ids.add(Long.valueOf(tuple.getValue()));
                // 4.2.获取分数(时间戳）
                long t = tuple.getScore().longValue();
                if(t == minTime){
                    os++;
                }else{
                    minTime = t;
                    os = 1;
                }
            }

            //我关注的人
            String userFollowKey = getUserFollow(userId.toString());
            Set<String> followUserIds = redisCache.getCacheZSet(userFollowKey);

            //从数据库筛选
            LambdaQueryWrapper<Talk> talkLambdaQueryWrapper = new LambdaQueryWrapper<>();

            talkLambdaQueryWrapper.in(Talk::getId,ids);
            talkLambdaQueryWrapper.in(Talk::getUserId,followUserIds);
            talkLambdaQueryWrapper.eq(Talk::getDelFlag,0);
            List<Talk> list = list(talkLambdaQueryWrapper);
            if(StringUtils.isNotEmpty(list)){
                list.sort((o1, o2) -> (o1.getCreateTime().getTime()<=o2.getCreateTime().getTime())?1:-1);
                setTalkInfo(list);
                talks.addAll(list);
                pageSize -= list.size();
            }
            //偏移多少
            offSet = os;
            //最后一条出现的分数
            time = minTime;

        }

        tableDataInfo.setTotal(redisCache.getZSetSize(key));
        tableDataInfo.setTime(time);
        tableDataInfo.setOffSet(offSet);
        return tableDataInfo;
    }

    private void setTalkInfo(List<Talk> list) {
        list.forEach(item->{
            //设置时间
            item.setDate(format(item.getCreateTime()));
            //封装评论
            setComment(item);
            //封装用户
            setUserVo(item);
            //封装图片
            setTalkPicture(item);
            //设置分类
            setType(item);
            //封装点赞量
            setTalkThumbNum(item);
            //判断是否点赞和收藏信息
            isThumbAndCollection(item);
        });
    }

    /**
     * 推模式：拉取投递箱的talk
     * @return
     */
    private List<Talk> getUserDropBoxTalk() {
        String userId1 = ((Long)SecurityUtils.getUserId()).toString();

        String userFollowKey = getUserFollow(userId1);
        //关注列表为空
        Set<String> followUserIds = redisCache.getCacheZSet(userFollowKey);

        if(StringUtils.isEmpty(followUserIds))
            return new ArrayList<>();

        //收件箱
        String key = getUserDropBox(userId1);

        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        return null;
    }

    private void setType(Talk talk){
        Type type = typeMapper.selectOne(new LambdaQueryWrapper<Type>()
                .eq(Type::getId,talk.getTypeId())
                .select(Type::getName));
        Optional.ofNullable(type).ifPresent(item->{
            talk.setTypeName(item.getName());
        });
    }

    /**
     * 获取用户收藏talk列表
     * @return
     */
    private Set<String> getStarCollection() {
        Long userId1 = (Long) SecurityUtils.getUserId();
        String key =  getUserTalkCollection(userId1.toString());
        Set<String> talkIds = redisCache.queryWithMutexAndBuild(key, (k) -> redisCache.getCacheSet(k), (k) -> {
            redisCacheBuilder.buildTalkStarCollectionCache(userId1, k);
            return redisCache.getCacheObject(k);
        });
        return talkIds;
    }



    /**
     * 判断是否点赞和收藏
     * @param item
     */
    private void isThumbAndCollection(Talk item) {
        item.setThumbs(0);
        item.setStar(0);
        //如果用户已登录
        try {

            String userId =((Long) SecurityUtils.getUserId()).toString();

            //判断用户是否点赞。这里保证数据库数据已经同步到redis
            if(redisCache.isZSetMember(getTalkThumbs(item.getId().toString()) , userId)){
                item.setThumbs(1);
            }

            //判断用户是否收藏
            String key =  getUserTalkCollection(userId);
            redisCache.consumerWithMutexAndBuild(key,(k)->{
                item.setStar(redisCache.isZSetMember(k,item.getId().toString())?1:0);
            },(k)->redisCacheBuilder.buildTalkStarCollectionCache((Long) SecurityUtils.getUserId(),k));

        } catch (Exception e) {
            //不处理
        }
    }

    /**
     * 封装用户UserVo
     * @param item
     */
    private void setUserVo(Talk item) {
        SysUser user = sysUserMapper.getNickNameAndAvatarByUserIdSysUser(item.getUserId());
        item.setUser(user);
    }


    /**
     * 通过id拿talk
     * @param talkId
     * @return
     */
    @Override
    public Talk getTalkById(String talkId) {

        LambdaQueryWrapper<Talk> talkLambdaQueryWrapper = new LambdaQueryWrapper<>();
        talkLambdaQueryWrapper.eq(Talk::getDelFlag,0);
        talkLambdaQueryWrapper.eq(Talk::getId,talkId);
        Talk talk = getOne(talkLambdaQueryWrapper);

        Optional.ofNullable(talk).ifPresent(item->{
            //封装图片
            setTalkPicture(item);
            //设置点赞量
            setTalkThumbNum(item);
            //封装用户
            setUserVo(item);
            //判断是否点赞和收藏信息
            isThumbAndCollection(item);
            //设置点赞列表
            setThumbList(item);
        });
        return talk;
    }

    @Transactional
    @Override
    public void addTalk(Talk talk, Long userId) {
        talk.setUserId(userId);
        talk.setCreateTime(new Date());
        save(talk);

        Optional.ofNullable(talk.getPictrueList()).ifPresent(item->{
            item.forEach(picture->{
                TalkPicture talkPicture = new TalkPicture();
                talkPicture.setTalkId(talk.getId());
                talkPicture.setUrl(picture);
                talkPicture.setPath(picture);
                talkPictureMapper.insert(talkPicture);
            });
        });

        //发送到粉丝的收件箱：
//        talkProducer.sendMsg(talk);
      //  delivery(talk, userId);

    }

    /**
     * 把talk推送给我的粉丝
     * @param talk
     * @param userId
     */
    @Deprecated
    private void delivery(Talk talk, Long userId) {
        //我的粉丝列表
        String key = getUserFans(userId.toString());
        Set<String> cacheZSet =  redisCache.queryWithMutexAndBuild(key,(k)-> redisCache.getCacheZSet(k),(k)->{
            redisCacheBuilder.buildFansCache(userId,k);
            return redisCache.getCacheZSet(k);
        });

        if(StringUtils.isNotEmpty(cacheZSet)){
            cacheZSet.forEach(item->{
                String userDropBoxKey = getUserDropBox(item);
                redisCache.consumerWithMutexAndBuild(userDropBoxKey,(k)->{
                    redisCache.setCacheZSet(k, talk.getId().toString(), talk.getCreateTime().getTime());
                },(k)->{
                    redisCacheBuilder.buildUserDropBox(Long.parseLong(item),k);
                });
            });
        }
    }

    /**
     * 点赞
     * @param talkId
     */
    @Override
    public void thumbs(Long talkId) {

        String userId = ((Long)SecurityUtils.getUserId()).toString();

        String key =  getTalkThumbs(talkId.toString());

        redisCache.consumerWithMutexAndBuild(key,(k)->{
            if(redisCache.isZSetMember(k,userId))
                redisCache.removeZSet(k,userId);
            else
                redisCache.setCacheZSet(k,userId,System.currentTimeMillis());
        },(k)->redisCacheBuilder.buildTalkThumbCache(talkId,k));
    }

    /**
     * 收藏
     * @param talkId
     */
    @Override
    public void collectionTalk(Long talkId) {
        if(talkId==-1L)
            return;
        Long userId1 = (Long) SecurityUtils.getUserId();
        String key =  getUserTalkCollection(userId1.toString());
        redisCache.consumerWithMutexAndBuild(key, (k) -> {
            if(redisCache.isZSetMember(k,talkId.toString()))
                redisCache.removeZSet(k,talkId.toString());
            else
                redisCache.setCacheZSet(k,talkId.toString(),System.currentTimeMillis());
        }, (k)-> redisCacheBuilder.buildTalkStarCollectionCache(userId1,k));
    }

    @Transactional
    @Override
    public void updateTalk(Talk talk) {

        updateById(talk);
        talkPictureMapper.delete(new LambdaQueryWrapper<TalkPicture>().eq(TalkPicture::getTalkId,talk.getId()));
        Optional.ofNullable(talk.getPictrueList()).ifPresent(item->{
            item.forEach(picture->{
                TalkPicture talkPicture = new TalkPicture();
                talkPicture.setTalkId(talk.getId());
                talkPicture.setUrl(picture);
                talkPicture.setPath(picture);
                talkPictureMapper.insert(talkPicture);
            });
        });
    }

    @Transactional
    @Override
    public void deleteTalks(List<Long> asList) {
        if(StringUtils.isNotEmpty(asList)){
            //删除所有图片
            talkPictureMapper.delete(new LambdaQueryWrapper<TalkPicture>().in(TalkPicture::getTalkId,asList));

            //删除所有评论
            talkCommentMapper.delete(new LambdaQueryWrapper<TalkComment>().in(TalkComment::getTalkId,asList));

            //删除所有帖子
            removeBatchByIds(asList);
        }
    }

    /**
     * 获取帖子
     * @param talkId
     * @return
     */
    @Override
    public Talk getTalk(Long talkId) {

        Talk talk = getById(talkId);
        setTalkPicture(talk);
        setUserVo(talk);
        return talk;
    }

    @Override
    public PageResult getCollectionTalk(Long time, Integer offSet, Integer pageSize) {

        PageResult tableDataInfo = new PageResult();
        ArrayList<Talk> talks = new ArrayList<>();
        tableDataInfo.setRows(talks);
        tableDataInfo.setIsLastPage(0);


        // 1.获取当前用户
        Long userId = (Long) SecurityUtils.getUserId();

        String key = getUserTalkCollection(userId.toString());

        while (pageSize>0){

            long minTime = 0;
            int os = 1;
            //拿一条数据
            Set<ZSetOperations.TypedTuple<String>> typedTuples =  redisCache.getCacheZSetByScoreWithScores(key, time , offSet , pageSize);
            //没有了，表示已经没有数据了
            if (StringUtils.isEmpty(typedTuples)) {
                tableDataInfo.setIsLastPage(1);
                break;
            }

            // 解析数据
            List<Long> ids = new ArrayList<>();
            for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
                // 4.1.获取id
                ids.add(Long.valueOf(tuple.getValue()));
                // 4.2.获取分数(时间戳）
                long t = tuple.getScore().longValue();
                if(t == minTime){
                    os++;
                }else{
                    minTime = t;
                    os = 1;
                }
            }

            for (int i = 0; i < ids.size(); i++) {
                //从数据库筛选
                LambdaQueryWrapper<Talk> talkLambdaQueryWrapper = new LambdaQueryWrapper<>();
                talkLambdaQueryWrapper.eq(Talk::getDelFlag,0);
                talkLambdaQueryWrapper.eq(Talk::getId,ids.get(i));
                Talk one = getOne(talkLambdaQueryWrapper);
                if(one!=null){
                    talks.add(one);
                    pageSize-=1;
                }
            }
            //偏移多少
            offSet = os;
            //最后一条出现的分数
            time = minTime;
        }
        setTalkInfo(talks);
        tableDataInfo.setTotal(redisCache.getZSetSize(key)-1);
        tableDataInfo.setTime(time);
        tableDataInfo.setOffSet(offSet);
        return tableDataInfo;
    }

    /**
     * 设置点赞列表
     * @param item
     */
    private void setThumbList(Talk item) {

        String key = getTalkThumbs(item.getId().toString());
        //只拿30个
        Set<String> userList = redisCache.getCacheZSet(key,1,30);
        if(StringUtils.isNotEmpty(userList)){
            LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sysUserLambdaQueryWrapper.in(SysUser::getUserId,userList);
            sysUserLambdaQueryWrapper.select(SysUser::getUserId,SysUser::getAvatar,SysUser::getNickName);
            List<UserVo> userVos = BeanCopyUtils.copyList(sysUserMapper.selectList(sysUserLambdaQueryWrapper), UserVo.class);
            item.setThumbsList(userVos);
        }
    }

    /**
     * 封装图片
     * @param item
     */
    private void setTalkPicture(Talk item) {
        List<String> collect = talkPictureMapper
                .selectList(new LambdaQueryWrapper<TalkPicture>()
                        .eq(TalkPicture::getTalkId, item.getId())
                        .orderByAsc(TalkPicture::getId)
                        .select(TalkPicture::getUrl))
                .stream().map(item2 -> item2.getUrl()).collect(Collectors.toList());
        item.setPictrueList(collect);
    }

    /**
     * 封装评论
     */
    private void setComment(Talk talk) {

        //查出最近5条评论
        Page<TalkComment> talkCommentPage = new Page<>();
        talkCommentPage.setSize(5);
        talkCommentPage.setCurrent(1);
        LambdaQueryWrapper<TalkComment> talkCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        talkCommentLambdaQueryWrapper.eq(TalkComment::getTalkId,talk.getId());
        talkCommentLambdaQueryWrapper.eq(TalkComment::getDelFlag,0);
        talkCommentLambdaQueryWrapper.orderByDesc(TalkComment::getCreateTime);
        Page<TalkComment> talkCommentPage1 = talkCommentMapper.selectPage(talkCommentPage, talkCommentLambdaQueryWrapper);
        if(talkCommentPage1.getTotal()!=0&talkCommentPage1.getRecords()!=null){
            talkCommentPage1.getRecords().forEach(item->{
                item.setUser(sysUserMapper.getNickNameAndAvatarByUserIdSysUser(item.getUserId()));
                if(item.getParentId()!=null){
                    TalkComment talkComment = talkCommentMapper.selectById(item.getParentId());
                    if(talkComment!=null)
                       item.setUserBy(sysUserMapper.getNickNameAndAvatarByUserIdSysUser(talkComment.getUserId()));
                }

            });
        }
        talk.setComment(talkCommentPage1);

    }

    /**
     * 设置点赞量.
     * 先从缓存中拿，如果缓存没有就重建缓存
     * @param talk
     */
    private void setTalkThumbNum(Talk talk) {

        String key = getTalkThumbs(talk.getId().toString());

        Long thumbsNum = redisCache.queryWithMutexAndBuild(key, (s) -> redisCache.getZSetSize(s), (s) -> {
            redisCacheBuilder.buildTalkThumbCache(talk.getId(), s);
            return redisCache.getZSetSize(s);
        });
        talk.setThumbsNum(thumbsNum.intValue() - 1);
    }




}




