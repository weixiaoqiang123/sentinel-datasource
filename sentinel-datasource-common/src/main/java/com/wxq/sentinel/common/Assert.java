package com.wxq.sentinel.common;

import com.wxq.sentinel.common.exception.SentinelException;

/**
 * @author weixiaoqiang
 * @date 2024/5/23
 **/
public class Assert {

    public static void nonNull(Object obj, String message) {
        nonNull(obj, new SentinelException(message));
    }

    public static void nonNull(Object obj, SentinelException e) {
        if(obj == null) {
            throw e;
        }
    }
}
