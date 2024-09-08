package com.xlf.framework.manager.factory;
import com.xlf.common.util.spring.SpringUtils;
import com.xlf.system.domain.SysOperLog;
import com.xlf.system.service.impl.SysOperLogServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 *
 * @author xlf
 */
public class AsyncFactory
{

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog operLog)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {

                SpringUtils.getBean(SysOperLogServiceImpl.class).insertOperlog(operLog);
            }
        };
    }
}
