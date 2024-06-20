package com.wxq.sentinel.redis;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.wxq.sentinel.common.datasource.DataSourceFactory;
import com.wxq.sentinel.common.properties.RedisConfigProperties;
import io.lettuce.core.RedisClient;

/**
 * @author weixiaoqiang
 * @date 2024/5/23
 **/
public class RedisDataSourceFactory extends DataSourceFactory<RedisConfigProperties> {

    private static final String RULE_PREFIX = "sentinel:rule:";

    private final String ruleKey;

    public RedisDataSourceFactory(String ruleName, RedisConfigProperties properties) {
        super(ruleName, properties);
        this.ruleKey = RULE_PREFIX + ruleName;
    }

    @Override
    public <T> WritableDataSource<T> createWritableDataSource(Converter<T, String> converter) {
        return new RedisWriteableDataSource<>(ruleKey, converter, writeLock, properties, createClient());
    }

    @Override
    public <T> ReadableDataSource<String, T> createReadableDataSource(Converter<String, T> parser) {
        return new RedisReadableDataSource<>(ruleKey, parser, readLock, properties, createClient());
    }

    private RedisClient createClient() {
        String host = properties.getHost();
        int port = properties.getPort();
        String url = "redis://" + host + ":" + port + "/" + properties.getPort();
        return RedisClient.create(url);
    }
}
