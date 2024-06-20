package com.wxq.sentinel.redis;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.wxq.sentinel.common.datasource.AbstractWritableDataSource;
import com.wxq.sentinel.common.exception.SentinelException;
import com.wxq.sentinel.common.properties.RedisConfigProperties;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.concurrent.locks.Lock;

/**
 * @author weixiaoqiang
 * @date 2024/5/23
 **/
public class RedisWriteableDataSource<T>  extends AbstractWritableDataSource<T, RedisConfigProperties> {

    private final RedisClient client;

    private final StatefulRedisConnection<String, String> connection;

    private final String ruleKey;

    public RedisWriteableDataSource(String ruleKey,
                                    Converter<T, String> parser,
                                    Lock writeLock,
                                    RedisConfigProperties properties,
                                    RedisClient client) {
        super(parser, writeLock, properties);
        this.ruleKey = ruleKey;
        this.client = client;
        connection = client.connect();
    }

    @Override
    public void write(T value) throws SentinelException {
        RedisCommands<String, String> commands = connection.sync();
        String rules = converter.convert(value);
        commands.set(ruleKey, rules);
    }

    @Override
    public void close() throws SentinelException {
        connection.close();
        client.shutdown();
    }
}
