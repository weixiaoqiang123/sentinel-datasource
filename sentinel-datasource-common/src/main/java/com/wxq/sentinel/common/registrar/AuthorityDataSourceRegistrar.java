package com.wxq.sentinel.common.registrar;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
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
public class AuthorityDataSourceRegistrar extends AbstractDataSourceRegistrar<List<AuthorityRule>> {

    public AuthorityDataSourceRegistrar(DataSourceFactory dataSourceFactory) {
        super(dataSourceFactory);
    }

    @Override
    public void registerReadDataSource() {
        AuthorityRuleManager.register2Property(readableDataSource.getProperty());
    }

    @Override
    public void registerWriteDataSource() {
        WritableDataSourceRegistry.registerAuthorityDataSource(writableDataSource);
    }

    @Override
    public Converter<String, List<AuthorityRule>> getParser() {
        return source -> JSON.parseObject(source, new TypeReference<List<AuthorityRule>>(){});
    }

    @Override
    public Converter<List<AuthorityRule>, String> getConverter() {
        return rules ->  JSON.toJSONString(rules, SerializerFeature.PrettyFormat);
    }
}
