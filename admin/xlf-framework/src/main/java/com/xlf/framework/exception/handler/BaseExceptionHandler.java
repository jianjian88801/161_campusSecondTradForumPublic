package com.xlf.framework.exception.handler;


import com.xlf.common.enums.StatusCodeEnum;
import com.xlf.common.exception.BizException;
import com.xlf.common.util.ResponseResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 异常处理控制器
 *
 * @author fangjiale 2021年01月26日
 */
@Slf4j
@ControllerAdvice
@Deprecated
public class BaseExceptionHandler {



    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public ResponseResult bizHandler(BizException e) {
        e.printStackTrace();
        log.debug(e.getMessage());
        System.out.println("业务异常");
        return new ResponseResult(e.getCode(),e.getMessage());
    }

//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public ResponseResult handler(Exception e) {
//        e.printStackTrace();
//        log.error("Exception:[{}]", e);
//       return new ResponseResult(StatusCodeEnum.FAIL.getCode(),e.getMessage());
//    }
}
