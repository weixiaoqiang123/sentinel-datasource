package com.wxq.sentinel.common.properties;

/**
 * @author weixiaoqiang
 * @date 2024/5/10
 **/
public class NacosConfigProperties {

    /**
     * A Nacos address
     */
    private String serverAddr;

    /**
     * The sentinel rule persists the namespace
     */
    private String namespace = "sentinel";

    /**
     * The sentinel rule persists the group
     */
    private String group = "DEFAULT_GROUP";

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
