package com.xlf.system.service;

import com.xlf.system.domain.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 小新
* @description 针对表【comment】的数据库操作Service
* @createDate 2023-02-23 21:53:05
*/
public interface CommentService extends IService<Comment> {

    void deleteById(Long commentId);
}
