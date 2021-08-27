package com.pk.mongotest.entity;

/**
 * 账号状态枚举
 *
 * @author Kipeng Huang <huangpeng08@baidu.com>
 * @since 2021/5/25 10:19 上午
 */

public enum AccountStatus {
    ENABLED("使用中"),
    // 所有功能不在有效期，且最后到期时间距今6个月以内
    DISABLED("已停用"),
    // 功能的有效期距今都超过6个月
    DEPRECATED("已废弃");
    private String desc;

    AccountStatus(String desc) {
        this.desc = desc;
    }


    public String getDesc() {
        return desc;
    }
}
