package com.xlf.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.common.security.util.SecurityUtils;
import com.xlf.common.util.BeanCopyUtils;
import com.xlf.common.util.StringUtils;
import com.xlf.system.domain.Sell;
import com.xlf.system.domain.SellPicture;
import com.xlf.system.domain.Topic;
import com.xlf.system.domain.vo.SellPictureVo;
import com.xlf.system.mapper.SellPictureMapper;
import com.xlf.system.mapper.SysUserMapper;
import com.xlf.system.service.SellService;
import com.xlf.system.mapper.SellMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.xlf.common.util.date.TimeAgoUtils.format;

/**
* @author 小新
* @description 针对表【sell】的数据库操作Service实现
* @createDate 2023-03-15 14:47:06
*/
@Service
public class SellServiceImpl extends ServiceImpl<SellMapper, Sell>
    implements SellService{

    @Resource
    SellMapper sellMapper;

    @Resource
    SysUserMapper userMapper;

    @Resource
    SellPictureMapper sellPictureMapper;

    @Override
    public List<Sell> getList(Integer orderType, String name) {


        LambdaQueryWrapper<Sell> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.isNotEmpty(name),Sell::getContent,name);

        wrapper.eq(Sell::getDelFlag,0);

        //根据发布时间排
        if(orderType==1){
            wrapper.and(i -> i.eq(Sell::getStatus, "1").or().eq(Sell::getStatus,null));
            wrapper.orderByDesc(Sell::getCreateTime);
            //我的
        }else if(orderType==2){
            wrapper.orderByDesc(Sell::getCreateTime);
            Long userId = (Long)SecurityUtils.getUserId();
            wrapper.eq(Sell::getUserId,userId.toString());
        }else {
            //已购
            wrapper.orderByDesc(Sell::getCreateTime);
            Long userId = (Long)SecurityUtils.getUserId();
            wrapper.eq(Sell::getUserById,userId.toString());
        }
        List<Sell> sells = sellMapper.selectList(wrapper);
        if(sells!=null){
            sells.forEach(item->{
                //设置时间
                item.setDate(format(item.getCreateTime()));
               item.setSysUser(userMapper.selectById(item.getUserId()));
            });
            sells.forEach(item->{
                item.setSellPictures(sellPictureMapper
                        .selectList(new LambdaQueryWrapper<SellPicture>()
                                .eq(SellPicture::getSellId,item.getId())
                                .orderByAsc(SellPicture::getId))
                        .stream().map(item2-> (BeanCopyUtils.copyObject(item2,SellPictureVo.class)))
                        .collect(Collectors.toList()));
            });
        }
        return sells;
    }

    @Override
    public Sell getSellById(Long sellId,String name) {

        LambdaQueryWrapper<Sell> sellLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sellLambdaQueryWrapper.eq(Sell::getId,sellId);
        sellLambdaQueryWrapper.eq(Sell::getDelFlag,0);
        if(StringUtils.isNotEmpty(name))
                sellLambdaQueryWrapper.like(Sell::getContent,name);
        Sell sell = sellMapper.selectOne(sellLambdaQueryWrapper);
        if(sell!=null) {
            sell.setSysUser(userMapper.selectById(sell.getUserId()));
            sell.setSellPictures(sellPictureMapper
                    .selectList(new LambdaQueryWrapper<SellPicture>()
                    .eq(SellPicture::getSellId,sell.getId())
                     .orderByAsc(SellPicture::getId))
                    .stream()
                    .map(item->BeanCopyUtils.copyObject(item,SellPictureVo.class))
                    .collect(Collectors.toList()));
        }
        return sell;
    }

    @Transactional
    @Override
    public void addSell(Sell sell) {
        sell.setUserId((Long) SecurityUtils.getUserId());
        sell.setDelFlag(0);
        //未卖出
        sell.setStatus("1");
        save(sell);

        sellPictureMapper.delete(new LambdaQueryWrapper<SellPicture>().eq(SellPicture::getSellId, sell.getId()));

        if(sell.getSellPictures()!=null){
            for (SellPictureVo sellPictureVo : sell.getSellPictures()) {
                SellPicture sellPicture = BeanCopyUtils.copyObject(sellPictureVo,SellPicture.class);
                sellPicture.setSellId(sell.getId());
                sellPictureMapper.insert(sellPicture);
            }
        }
    }


}




