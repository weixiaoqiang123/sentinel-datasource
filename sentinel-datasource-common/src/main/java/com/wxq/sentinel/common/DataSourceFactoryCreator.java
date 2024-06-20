package com.wxq.sentinel.common;

import com.wxq.sentinel.common.datasource.DataSourceFactory;

/**
 * @author weixiaoqiang
 * @date 2024/5/22
 **/
public interface DataSourceFactoryCreator {

    DataSourceFactory create(String ruleName, Object properties);
}
