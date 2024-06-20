package com.wxq.sentinel.common.datasource;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.wxq.sentinel.common.exception.SentinelException;
import java.util.concurrent.locks.Lock;

/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public abstract class AbstractWritableDataSource<T, O> implements WritableDataSource<T> {

    protected final O properties;

    protected final Converter<T, String> converter;

    protected final Lock writeLock;

    public AbstractWritableDataSource(Converter<T, String> converter, Lock writeLock, O properties){
        this.converter = converter;
        this.writeLock = writeLock;
        this.properties = properties;
    }

    @Override
    public void write(T value) throws SentinelException {
        throw new SentinelException("need override");
    }

    @Override
    public void close() throws SentinelException {
    }
}
