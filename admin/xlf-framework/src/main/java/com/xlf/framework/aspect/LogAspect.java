package com.xlf.framework.aspect;//package com.xlf.framework.aspect;
//
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Arrays;
//
////controller日志
//@Aspect
//@Component
//@Slf4j
//public class LogAspect {
//
//    @Pointcut("execution(* com.xlf.controller.*..*(..))")
//    public void log(){
//    }
//    @Before("log()")
//    public void doBefore(JoinPoint joinPoint) {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        String url = request.getRequestURL().toString();
//        String ip = request.getRemoteAddr();
//        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
//        Object[] args = joinPoint.getArgs();
//        RequestLog requestLog = new RequestLog(url, ip, classMethod, args);
//        log.info("Request : {}", requestLog);
//
//    }
//
//    /**
//     * 和下面的区别就是这个无论如何都会执行
//     */
//    @After("log()")
//    public void after(){
//        log.info("-----doAfter");
//    }
//
//    /**
//     * 后置通知
//     * @param result
//     */
//    @AfterReturning(returning = "result",pointcut = "log()")
//    public void doAfterReturn(Object result){
//        log.info("Result : {}",result.toString());
//    }
//
//    private class RequestLog {
//        private String url;
//        private String ip;
//        private String classMethod;
//        private Object[] args;
//
//        public RequestLog(String url, String ip, String classMethod, Object[] args) {
//            this.url = url;
//            this.ip = ip;
//            this.classMethod = classMethod;
//            this.args = args;
//        }
//
//        @Override
//        public String toString() {
//            return "{" +
//                    "url='" + url + '\'' +
//                    ", ip='" + ip + '\'' +
//                    ", classMethod='" + classMethod + '\'' +
//                    ", args=" + Arrays.toString(args) +
//                    '}';
//        }
//    }
//}
