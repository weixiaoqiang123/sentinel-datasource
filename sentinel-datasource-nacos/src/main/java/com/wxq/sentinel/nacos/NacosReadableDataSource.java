package com.wxq.sentinel.nacos;

import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.wxq.sentinel.common.datasource.AbstractReadableDataSource;
import com.wxq.sentinel.common.exception.SentinelException;
import com.wxq.sentinel.common.properties.NacosConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public class NacosReadableDataSource<T> extends AbstractReadableDataSource<T, NacosConfigProperties> {

    private static final Logger logger = LoggerFactory.getLogger(NacosReadableDataSource.class);

    private final ExecutorService pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), new NamedThreadFactory("sentinel-nacos-ds-update", true),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private final NacosListener<T> listener;

    private final String namespace;

    private final String group;

    private final String dataId;

    private final ConfigService configService;

    public NacosReadableDataSource(String dataId, ConfigService configService,
                                   Converter<String, T> parser, Lock readLock,
                                   NacosConfigProperties properties) {
        super(parser, readLock, properties);
        this.listener = new NacosListener<>(pool, this);
        this.namespace = properties.getNamespace();
        this.group = properties.getGroup();
        this.dataId = dataId;
        this.configService = configService;
        try {
            configService.addListener(group, dataId, listener);
        } catch (NacosException e) {
            throw new SentinelException("Add nacos listener failed", e);
        }
        updateConfig();
        logger.info("The configuration was successfully loaded for the first time");
    }

    @Override
    public String readSource() throws SentinelException {
        readLock.lock();
        try {
            return configService.getConfig(group, dataId, 5000);
        }catch (NacosException e) {
            throw new SentinelException("Read config failed: " + dataId);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void close() throws SentinelException {
        pool.shutdown();
    }
}
