package com.wxq.sentinel.common.registrar;

import com.alibaba.csp.sentinel.command.handler.ModifyParamFlowRulesCommandHandler;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wxq.sentinel.common.datasource.DataSourceFactory;

import java.util.List;

/**
 * @author weixiaoqiang
 * @date 2024/5/13
 **/
public class ParamDataSourceRegistrar extends AbstractDataSourceRegistrar<List<ParamFlowRule>> {

    public ParamDataSourceRegistrar(DataSourceFactory dataSourceFactory) {
        super(dataSourceFactory);
    }

    @Override
    public void registerReadDataSource() {
        ParamFlowRuleManager.register2Property(readableDataSource.getProperty());
    }

    @Override
    public void registerWriteDataSource() {
        ModifyParamFlowRulesCommandHandler.setWritableDataSource(writableDataSource);
    }

    @Override
    public Converter<List<ParamFlowRule>, String> getConverter() {
        return value -> JSON.toJSONString(value, SerializerFeature.PrettyFormat);
    }

    @Override
    public Converter<String, List<ParamFlowRule>> getParser() {
        return source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>(){});
    }
}
