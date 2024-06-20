package com.wxq.sentinel.common.registrar;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wxq.sentinel.common.datasource.DataSourceFactory;
import java.util.List;

/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public class DegradeDataSourceRegistrar extends AbstractDataSourceRegistrar<List<DegradeRule>> {

    public DegradeDataSourceRegistrar(DataSourceFactory dataSourceFactory) {
        super(dataSourceFactory);
    }

    @Override
    public void registerReadDataSource() {
        DegradeRuleManager.register2Property(readableDataSource.getProperty());
    }

    @Override
    public void registerWriteDataSource() {
        WritableDataSourceRegistry.registerDegradeDataSource(writableDataSource);
    }

    @Override
    public Converter<String, List<DegradeRule>> getParser() {
        return source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>(){});
    }

    @Override
    public Converter<List<DegradeRule>, String> getConverter() {
        return value -> JSON.toJSONString(value, SerializerFeature.PrettyFormat);
    }
}
