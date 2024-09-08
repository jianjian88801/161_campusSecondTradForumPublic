package com.xlf.system.service;

import com.xlf.system.domain.SysOperLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 小新
* @description 针对表【sys_oper_log(操作日志记录)】的数据库操作Service
* @createDate 2023-05-23 09:02:53
*/
public interface SysOperLogService extends IService<SysOperLog> {


    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    public void insertOperlog(SysOperLog operLog);

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    public List<SysOperLog> selectOperLogList(SysOperLog operLog);

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public int deleteOperLogByIds(Long[] operIds);

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    public SysOperLog selectOperLogById(Long operId);

    /**
     * 清空操作日志
     */
    public void cleanOperLog();

}
