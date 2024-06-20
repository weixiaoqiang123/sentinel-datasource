package com.wxq.sentinel.common.datasource;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author weixiaoqiang
 * @date 2024/5/22
 **/
public abstract class DataSourceFactory<O> {

    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected final Lock readLock = lock.readLock();
    protected final Lock writeLock = lock.writeLock();
    protected final String ruleName;
    protected final O properties;

    public DataSourceFactory(String ruleName, O properties) {
        this.ruleName = ruleName;
        this.properties = properties;
    }

    public abstract <T> WritableDataSource<T> createWritableDataSource(Converter<T, String> converter);

    public abstract <T> ReadableDataSource<String, T> createReadableDataSource(Converter<String, T> parser);
}
