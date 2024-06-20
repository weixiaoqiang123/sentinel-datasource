package com.wxq.sentinel.common.properties;

/**
 * @author weixiaoqiang
 * @date 2024/5/23
 **/
public class RedisConfigProperties {

    private String host = "localhost";

    private int port = 6379;

    private String username;

    private String password;

    private int database = 0;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }
}
