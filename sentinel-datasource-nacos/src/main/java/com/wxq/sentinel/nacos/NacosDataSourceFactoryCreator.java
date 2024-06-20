package com.wxq.sentinel.nacos;

import com.wxq.sentinel.common.DataSourceFactoryCreator;
import com.wxq.sentinel.common.datasource.DataSourceFactory;

/**
 * @author weixiaoqiang
 * @date 2024/5/13
 **/
public class NacosDataSourceFactoryCreator implements DataSourceFactoryCreator {

    @Override
    public DataSourceFactory create(String ruleName, Object properties) {
        return new NacosDataSourceFactory(ruleName, properties);
    }
}
