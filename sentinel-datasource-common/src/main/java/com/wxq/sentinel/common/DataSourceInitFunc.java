package com.wxq.sentinel.common;

import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.util.SpiLoader;
import com.wxq.sentinel.common.datasource.DataSourceFactory;
import com.wxq.sentinel.common.registrar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weixiaoqiang
 * @date 2024/5/23
 **/
public class DataSourceInitFunc implements InitFunc {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceInitFunc.class);

    private static Object dataSourceConfig;

    @Override
    public void init() throws Exception {
        Assert.nonNull(dataSourceConfig, "Not found a sentinel dataSource");
        logger.info("Starting initializing dataSource...");
        try {
            DataSourceFactoryCreator factoryCreator = SpiLoader.loadFirstInstance(DataSourceFactoryCreator.class);
            Assert.nonNull(factoryCreator, "Not found dataSourceFactory creator");
            for (RuleType ruleType : RuleType.values()) {
                DataSourceFactory dataSourceFactory = factoryCreator.create(ruleType.getType(), dataSourceConfig);
                DataSourceRegistrar registrar = null;
                switch (ruleType) {
                    case FLOW: registrar = new FlowDataSourceRegistrar(dataSourceFactory); break;
                    case DEGRADE: registrar = new DegradeDataSourceRegistrar(dataSourceFactory); break;
                    case PARAMETER: registrar = new ParamDataSourceRegistrar(dataSourceFactory); break;
                    case SYSTEM: registrar = new SystemDataSourceRegistrar(dataSourceFactory); break;
                    case AUTHORITY: registrar = new AuthorityDataSourceRegistrar(dataSourceFactory); break;
                }
                registrar.register();
                logger.info("DataSource registered for rule type: {}", ruleType.getType());
            }
            logger.info("Registration of the sentinel data source is complete");
        } catch (Exception e) {
            logger.error("Failed to initialize dataSource.", e);
            throw e;
        } finally {
            dataSourceConfig = null;
        }
    }

    public static void setDataSourceConfig(Object properties) {
        dataSourceConfig = properties;
    }
}
