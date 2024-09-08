package com.xlf.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.common.util.StringUtils;
import com.xlf.system.domain.Comment;
import com.xlf.system.service.CommentService;
import com.xlf.system.mapper.CommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author 小新
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2023-02-23 21:53:05
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{



    @Transactional
    @Override
    public void deleteById(Long commentId) {
        dfsDeleteById(commentId);
    }

    @Transactional
    public void dfsDeleteById(Long id){

        List<Long> collect = list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getParentId, id))
                .stream().map(item -> item.getId())
                .collect(Collectors.toList());

        removeById(id);
        if(StringUtils.isEmpty(collect))
                return;

        collect.forEach(item->{
            dfsDeleteById(item);
        });
    }
}




