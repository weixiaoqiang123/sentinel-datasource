package com.wxq.sentinel.redis;

import com.wxq.sentinel.common.datasource.AbstractReadableDataSource;
import io.lettuce.core.pubsub.RedisPubSubListener;

/**
 * @author weixiaoqiang
 * @date 2024/5/24
 **/
public class RedisListener implements RedisPubSubListener<String, String> {

    private String channel;

    private AbstractReadableDataSource readableDataSource;

    public RedisListener(String channel, AbstractReadableDataSource dataSource) {
        this.channel = channel;
        this.readableDataSource = dataSource;
    }

    @Override
    public void message(String channel, String message) {
        if(this.channel.equals(channel)) {
            readableDataSource.updateConfig(message);
        }
    }

    @Override
    public void message(String s, String k1, String s2) {

    }

    @Override
    public void subscribed(String s, long l) {

    }

    @Override
    public void psubscribed(String s, long l) {

    }

    @Override
    public void unsubscribed(String s, long l) {

    }

    @Override
    public void punsubscribed(String s, long l) {

    }
}
