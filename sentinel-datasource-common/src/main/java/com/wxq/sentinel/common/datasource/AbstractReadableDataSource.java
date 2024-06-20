package com.wxq.sentinel.common.datasource;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.wxq.sentinel.common.Assert;
import com.wxq.sentinel.common.exception.SentinelException;
import java.util.concurrent.locks.Lock;

/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public abstract class AbstractReadableDataSource<T, O> implements ReadableDataSource<String, T> {

    protected final Converter<String, T> parser;

    protected final SentinelProperty<T> property;

    protected final Lock readLock;

    protected final O properties;

    public AbstractReadableDataSource(Converter<String, T> parser, Lock readLock, O properties) {
        Assert.nonNull(parser, "parser can't be null");
        this.parser = parser;
        this.readLock = readLock;
        this.properties = properties;
        this.property = new DynamicSentinelProperty<T>();
    }

    @Override
    public T loadConfig() throws SentinelException {
        return loadConfig(readSource());
    }

    @Override
    public String readSource() throws SentinelException {
        throw new SentinelException("need override");
    }

    @Override
    public void close() throws SentinelException {
    }

    @Override
    public SentinelProperty<T> getProperty() {
        return property;
    }

    protected T loadConfig(String source) {
        return parser.convert(source);
    }

    public final void updateConfig() throws SentinelException {
        updateConfig(readSource());
    }

    public final void updateConfig(String source) throws SentinelException {
        getProperty().updateValue(loadConfig(source));
    }
}