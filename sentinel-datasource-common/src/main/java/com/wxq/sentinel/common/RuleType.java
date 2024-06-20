package com.wxq.sentinel.common;

/**
 * @author weixiaoqiang
 * @date 2024/5/13
 **/
public enum RuleType {

    FLOW("flowRule"),

    DEGRADE("degradeRule"),

    PARAMETER("paramRule"),

    SYSTEM("systemRule"),

    AUTHORITY("authorityRule")
    ;

    private String type;

    RuleType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
