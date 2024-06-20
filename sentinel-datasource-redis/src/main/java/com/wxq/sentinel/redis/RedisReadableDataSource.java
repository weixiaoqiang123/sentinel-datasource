package com.wxq.sentinel.redis;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.wxq.sentinel.common.datasource.AbstractReadableDataSource;
import com.wxq.sentinel.common.exception.SentinelException;
import com.wxq.sentinel.common.properties.RedisConfigProperties;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import java.util.concurrent.locks.Lock;

/**
 * @author weixiaoqiang
 * @date 2024/5/23
 **/
public class RedisReadableDataSource<T> extends AbstractReadableDataSource<T, RedisConfigProperties> {

    private final RedisClient client;

    private final String ruleKey;

    private final StatefulRedisConnection<String, String> connection;

    private final StatefulRedisPubSubConnection<String, String> subscribeConnection;

    public RedisReadableDataSource(String ruleKey,
                                   Converter<String, T> parser,
                                   Lock readLock,
                                   RedisConfigProperties properties,
                                   RedisClient client) {
        super(parser, readLock, properties);
        this.ruleKey = ruleKey;
        this.client = client;
        this.connection = client.connect();
        this.subscribeConnection = client.connectPubSub();
        RedisPubSubCommands<String, String> pubSubCommands = this.subscribeConnection.sync();
        String channel = "__keyspace@" + properties.getDatabase() + "__:" + ruleKey;
        pubSubCommands.subscribe(channel);
        subscribeConnection.addListener(new RedisListener(channel, this));
    }

    @Override
    public String readSource() throws SentinelException {
        RedisCommands<String, String> commands = connection.sync();
        return commands.get(ruleKey);
    }

    @Override
    public void close() throws SentinelException {
        connection.close();
        subscribeConnection.close();
        client.shutdown();
    }
}
