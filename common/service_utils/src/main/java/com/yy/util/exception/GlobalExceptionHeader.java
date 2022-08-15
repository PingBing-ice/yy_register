package com.yy.util.exception;


import com.yy.util.result.R;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.PoolException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author ice
 * @date 2022/6/19 18:21
 */
// 错误处理器,默认在这里
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHeader {


    @ExceptionHandler(RException.class)
    public R runExceptionHeader(RException e) {
        return R.error().message(e.getMessage());
    }
    @ExceptionHandler(RuntimeException.class)
    public R runExceptionHeader(RuntimeException e) {
        log.info("runException",e);
        return R.error().message("连接超时,请刷新页面");
    }

    @ExceptionHandler({PoolException.class,RedisSystemException.class})
    public R PoolException(PoolException p) {
        return R.error().message("连接超时,请刷新页面");
    }
    @ExceptionHandler(Exception.class)
    public R allException(Exception e) {
        log.info("Exception",e);
        return R.error().message("系统错误");
    }


}
