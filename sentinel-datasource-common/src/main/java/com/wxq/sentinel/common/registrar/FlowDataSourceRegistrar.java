package com.wxq.sentinel.common.registrar;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
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
public class FlowDataSourceRegistrar extends AbstractDataSourceRegistrar<List<FlowRule>> {

    public FlowDataSourceRegistrar(DataSourceFactory factory) {
        super(factory);
    }

    @Override
    public void registerReadDataSource() {
        FlowRuleManager.register2Property(readableDataSource.getProperty());
    }

    @Override
    public void registerWriteDataSource() {
        WritableDataSourceRegistry.registerFlowDataSource(writableDataSource);
    }

    @Override
    public Converter<List<FlowRule>, String> getConverter() {
        return value -> JSON.toJSONString(value, SerializerFeature.PrettyFormat);
    }

    @Override
    public Converter<String, List<FlowRule>> getParser() {
        return source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>(){});
    }
}
