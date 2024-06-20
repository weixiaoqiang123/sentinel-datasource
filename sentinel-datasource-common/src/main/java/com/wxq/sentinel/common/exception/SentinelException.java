package com.wxq.sentinel.common.exception;

/**
 * @author weixiaoqiang
 * @date 2024/5/22
 **/
public class SentinelException extends RuntimeException {

    public SentinelException(String message) {
        super(message);
    }

    public SentinelException(String message, Throwable cause) {
        super(message, cause);
    }
}
