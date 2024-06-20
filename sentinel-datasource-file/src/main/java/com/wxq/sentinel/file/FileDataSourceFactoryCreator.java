package com.wxq.sentinel.file;

import com.wxq.sentinel.common.datasource.DataSourceFactory;
import com.wxq.sentinel.common.DataSourceFactoryCreator;

/**
 * @author weixiaoqiang
 * @date 2024/5/13
 **/
public class FileDataSourceFactoryCreator implements DataSourceFactoryCreator {

    @Override
    public DataSourceFactory create(String ruleName, Object properties) {
        return new FileDataSourceFactory(ruleName, properties);
    }
}
