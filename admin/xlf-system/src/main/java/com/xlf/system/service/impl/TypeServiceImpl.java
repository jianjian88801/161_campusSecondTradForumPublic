package com.xlf.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.common.exception.ServiceException;
import com.xlf.common.pojo.domain.system.SysRole;
import com.xlf.common.pojo.domain.system.SysRoleMenu;
import com.xlf.common.util.StringUtils;
import com.xlf.system.domain.Talk;
import com.xlf.system.domain.Type;
import com.xlf.system.mapper.TalkMapper;
import com.xlf.system.service.TypeService;
import com.xlf.system.mapper.TypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
* @author 小新
* @description 针对表【type】的数据库操作Service实现
* @createDate 2023-04-11 19:25:53
*/
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type>
    implements TypeService{


    @Resource
    TypeMapper typeMapper;

    @Resource
    TalkMapper talkMapper;


    @Override
    public List<Type> getPage(String name,Integer status) {
        LambdaQueryWrapper<Type> typeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        typeLambdaQueryWrapper.orderByAsc(Type::getSort);
//        typeLambdaQueryWrapper.eq(Type::getStatus,0);
        typeLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Type::getName,name);
        typeLambdaQueryWrapper.eq(status!=null,Type::getStatus,status);
        List<Type> list = list(typeLambdaQueryWrapper);

        if(StringUtils.isNotEmpty(list)){
            list.forEach(item->{
                item.setNum(getTalkNum(item.getId()));
            });
        }

        return list;
    }

    private int getTalkNum(Long id) {
        return talkMapper.selectCount(new LambdaQueryWrapper<Talk>()
                .eq(Talk::getTypeId, id)
                .eq(Talk::getDelFlag,0)).intValue();
    }

    @Override
    public String checkMenuNameUnique(Type type) {
        return typeMapper.selectCount(new LambdaQueryWrapper<Type>()
                .eq(Type::getName,type.getName()))>0L?"1":"0";
    }

    @Override
    public Boolean deleteTypeByIds(Long[] typeIds) {
        for (Long typeId : typeIds)
        {
            if (getTalkNum(typeId) > 0)
            {
                throw new ServiceException("分类下存在帖子！不可删除");
            }
        }

        return removeByIds(Arrays.asList(typeIds));
    }
}




