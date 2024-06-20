package com.wxq.sentinel.common.properties;


/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public class FileConfigProperties {

    /**
     * File dataSource persistence directory
     */
    private String location;

    /**
     * File extension
     */
    private String extension = "json";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
