package com.wxq.sentinel.common.registrar;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wxq.sentinel.common.datasource.DataSourceFactory;

import java.util.List;

/**
 * @author weixiaoqiang
 * @date 2024/5/13
 **/
public class SystemDataSourceRegistrar extends AbstractDataSourceRegistrar<List<SystemRule>> {

    public SystemDataSourceRegistrar(DataSourceFactory dataSourceFactory) {
        super(dataSourceFactory);
    }

    @Override
    public void registerReadDataSource() {
        SystemRuleManager.register2Property(readableDataSource.getProperty());
    }

    @Override
    public void registerWriteDataSource() {
        WritableDataSourceRegistry.registerSystemDataSource(writableDataSource);
    }

    @Override
    public Converter<String, List<SystemRule>> getParser() {
        return source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>(){});
    }

    @Override
    public Converter<List<SystemRule>, String> getConverter() {
        return value -> JSON.toJSONString(value, SerializerFeature.PrettyFormat);
    }
}
