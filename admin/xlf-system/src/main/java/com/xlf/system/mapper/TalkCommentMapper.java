package com.xlf.system.mapper;

import com.xlf.system.domain.TalkComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 小新
* @description 针对表【talk_comment】的数据库操作Mapper
* @createDate 2023-04-11 19:36:13
* @Entity com.xlf.system.domain.TalkComment
*/
public interface TalkCommentMapper extends BaseMapper<TalkComment> {


    @Select("select * from talk_comment as t1  where t1.parent_id in " +
            "(select t2.id from talk_comment as t2 where #{userId} = t2.user_id and t2.del_flag=0)" +
            " ORDER BY  t1.create_time DESC")
    List<TalkComment> getTalkCommentByUserById(Long userId);
}




