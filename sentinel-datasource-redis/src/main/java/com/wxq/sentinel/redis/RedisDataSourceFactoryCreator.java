package com.wxq.sentinel.redis;

import com.wxq.sentinel.common.DataSourceFactoryCreator;
import com.wxq.sentinel.common.datasource.DataSourceFactory;
import com.wxq.sentinel.common.properties.RedisConfigProperties;

/**
 * @author weixiaoqiang
 * @date 2024/5/23
 **/
public class RedisDataSourceFactoryCreator implements DataSourceFactoryCreator {

    @Override
    public DataSourceFactory create(String ruleName, Object properties) {
        return new RedisDataSourceFactory(ruleName, (RedisConfigProperties) properties);
    }
}
