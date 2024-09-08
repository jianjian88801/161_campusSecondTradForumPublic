package com.xlf.framework.config;


/**

    corePoolSize：
    // 核心线程池大小 核心线程会一直存活，及时没有任务需要执行

    maxPoolSize:
    // 最大可创建的线程数 当线程数>=corePoolSize，且任务队列已满时。线程池会创建新线程来处理任务
    //  当线程数=maxPoolSize，且任务队列已满时，线程池会拒绝处理任务而抛出异常
    queueCapacity:
    // 队列最大长度 当核心线程数达到最大时，新任务会放在队列中排队等待执行

    keepAliveSeconds:
    // 线程池维护线程所允许的空闲时间  当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数量=corePoolSize
    // 如果allowCoreThreadTimeout=true，则会直到线程数量=0
    allowCoreThreadTimeout：允许核心线程超时
    rejectedExecutionHandler：任务拒绝处理器
        * 两种情况会拒绝处理任务：
        - 当线程数已经达到maxPoolSize，切队列已满，会拒绝新任务
            - 当线程池被调用shutdown()后，会等待线程池里的任务执行完毕，再shutdown。如果在调用shutdown()和线程池真正shutdown之间提交任务，会拒绝新任务
        * 线程池会调用rejectedExecutionHandler来处理这个任务。如果没有设置默认是AbortPolicy，会抛出异常
        * ThreadPoolExecutor类有几个内部实现类来处理这类情况：
        - AbortPolicy 丢弃任务，抛运行时异常
            - CallerRunsPolicy 执行任务
            - DiscardPolicy 忽视，什么都不会发生
            - DiscardOldestPolicy 从队列中踢出最先进入队列（最后一个执行）的任务
        * 实现RejectedExecutionHandler接口，可自定义处理器


//    线程池按以下行为执行任务
//    1. 当线程数小于核心线程数时，创建线程。
//    2. 当线程数大于等于核心线程数，且任务队列未满时，将任务放入任务队列。
//    3. 当线程数大于等于核心线程数，且任务队列已满
//      -1 若线程数小于最大线程数，创建线程
//      -2 若线程数等于最大线程数，抛出异常，拒绝任务


//     - corePoolSize = 每秒需要多少个线程处理？
//        * 一颗CPU核心同一时刻只能执行一个线程，然后操作系统切换上下文，核心开始执行另一个线程的代码，以此类推，超过cpu核心数，就会放入队列，如果队列也满了，就另起一个新的线程执行，所有推荐：corePoolSize = ((cpu核心数 * 2) + 有效磁盘数)，java可以使用Runtime.getRuntime().availableProcessors()获取cpu核心数
//            - queueCapacity = (coreSizePool/taskcost)*responsetime
//                * 计算可得 queueCapacity = corePoolSize/0.1*1。意思是队列里的线程可以等待1s，超过了的需要新开线程来执行
//                * 切记不能设置为Integer.MAX_VALUE，这样队列会很大，线程数只会保持在corePoolSize大小，当任务陡增时，不能新开线程来执行，响应时间会随之陡增。
//        - maxPoolSize = (max(tasks)- queueCapacity)/(1/taskcost)
//        * 计算可得 maxPoolSize = (1000-corePoolSize)/10，即(每秒并发数-corePoolSize大小) / 10
//        * （最大任务数-队列容量）/每个线程每秒处理能力 = 最大线程数
//            - rejectedExecutionHandler：根据具体情况来决定，任务不重要可丢弃，任务重要则要利用一些缓冲机制来处理
//            - keepAliveTime和allowCoreThreadTimeout采用默认通常能满足

**/
import com.xlf.common.util.ThreadUtils;
import com.xlf.common.properties.ThreadPoolProperties;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author ican
 * @date 2022/12/05 11:12
 **/
@Configuration
public class ThreadPoolConfig {

    @Autowired
    private ThreadPoolProperties threadPoolProperties;

    /**
     * 创建线程池
     *
     * @return 线程池
     */
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程池大小 核心线程会一直存活，及时没有任务需要执行
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        // 最大可创建的线程数 当线程数>=corePoolSize，且任务队列已满时。线程池会创建新线程来处理任务
        // 当线程数=maxPoolSize，且任务队列已满时，线程池会拒绝处理任务而抛出异常
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        // 等待队列最大长度 当核心线程数达到最大时，新任务会放在队列中排队等待执行
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        // 线程池维护线程所允许的空闲时间 当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数量=corePoolSize
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        // 线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 执行周期性或定时任务
     * 解决策略：在当前线程执行任务
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(threadPoolProperties.getCorePoolSize(),
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build(),
                new ThreadPoolExecutor.CallerRunsPolicy()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                ThreadUtils.printException(r, t);
            }
        };
    }

}
