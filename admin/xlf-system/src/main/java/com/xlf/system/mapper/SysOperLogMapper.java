package com.xlf.system.mapper;

import com.xlf.system.domain.SysOperLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 小新
* @description 针对表【sys_oper_log(操作日志记录)】的数据库操作Mapper
* @createDate 2023-05-23 09:02:53
* @Entity com.xlf.system.domain.SysOperLog
*/
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

    List<SysOperLog> selectOperLogList(SysOperLog operLog);

    void cleanOperLog();
}




