package com.wxq.sentinel.common.properties;


/**
 * @author weixiaoqiang
 * @date 2024/5/10
 **/
public class DataSourceProperties {

    /**
     * File dataSource config
     */
    private FileConfigProperties file;

    /**
     * Nacos dataSource config
     */
    private NacosConfigProperties nacos;

    public void setFile(FileConfigProperties file) {
        this.file = file;
    }

    public FileConfigProperties getFile() {
        return file;
    }

    public NacosConfigProperties getNacos() {
        return nacos;
    }

    public void setNacos(NacosConfigProperties nacos) {
        this.nacos = nacos;
    }
}
