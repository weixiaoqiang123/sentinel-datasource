package com.wxq.sentinel.common.registrar;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.wxq.sentinel.common.datasource.DataSourceFactory;

/**
 * @author weixiaoqiang
 * @date 2024/5/13
 **/
public abstract class AbstractDataSourceRegistrar<T> implements DataSourceRegistrar {

    protected ReadableDataSource<String, T> readableDataSource;

    protected WritableDataSource<T> writableDataSource;

    public AbstractDataSourceRegistrar(DataSourceFactory dataSourceFactory) {
        readableDataSource = dataSourceFactory.createReadableDataSource(getParser());
        writableDataSource = dataSourceFactory.createWritableDataSource(getConverter());
    }

    @Override
    public void register() {
        registerReadDataSource();
        registerWriteDataSource();
    }

    protected abstract void registerReadDataSource();

    protected abstract void registerWriteDataSource();

    protected abstract Converter<T, String> getConverter();

    protected abstract Converter<String, T> getParser();
}
