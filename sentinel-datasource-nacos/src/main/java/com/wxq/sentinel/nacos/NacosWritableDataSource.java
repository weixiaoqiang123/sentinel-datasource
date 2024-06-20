package com.wxq.sentinel.nacos;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.wxq.sentinel.common.datasource.AbstractWritableDataSource;
import com.wxq.sentinel.common.exception.SentinelException;
import com.wxq.sentinel.common.properties.NacosConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.locks.Lock;

/**
 * todo 抽取初始化目录逻辑 将目录初始化与文件初始化逻辑分开
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public class NacosWritableDataSource<T> extends AbstractWritableDataSource<T, NacosConfigProperties> {

    private static final Logger logger = LoggerFactory.getLogger(NacosWritableDataSource.class);

    private final ConfigService configService;

    private final String namespace;

    private final String groupId;

    private final String dataId;


    public NacosWritableDataSource(String dataId, ConfigService configService, Converter<T, String> parser,
                                   Lock writeLock, NacosConfigProperties properties) {
        super(parser, writeLock, properties);
        this.namespace = properties.getNamespace();
        this.groupId = properties.getGroup();
        this.dataId = dataId;
        this.configService = configService;
    }

    @Override
    public void write(T t) throws SentinelException {
        String config = converter.convert(t);
        writeLock.lock();
        try {
            configService.publishConfig(groupId, dataId, config, "json");
            logger.info("write {} successful", dataId);
        } catch (NacosException e) {
            String message = "Write config failed, namespace: %s, group: %s, dataId: %s";
            throw new SentinelException(String.format(message, namespace, groupId, dataId));
        }finally {
            writeLock.unlock();
        }
    }

}
