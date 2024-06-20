package com.wxq.sentinel.nacos;

import com.alibaba.nacos.api.config.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Executor;

/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public class NacosListener<T> implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(NacosListener.class);

    private Executor executor;

    private NacosReadableDataSource<T> dataSource;

    public NacosListener(Executor executor, NacosReadableDataSource<T> dataSource) {
        this.executor = executor;
        this.dataSource = dataSource;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public void receiveConfigInfo(String source) {
        dataSource.updateConfig(source);
        logger.info("Listening for configuration changes from the server, config: {}", source);
    }
}
