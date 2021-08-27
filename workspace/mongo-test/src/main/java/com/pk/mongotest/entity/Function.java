package com.pk.mongotest.entity;

/**
 * 意图提供的功能服务
 *
 * @author Kipeng Huang <huangpeng08@baidu.com>
 * @since 2021/6/7 4:41 下午
 */
public enum Function {
    SUSPECT("疑似甄别"),
    WARN("智能管控"),
    INTEL_EDITION("智能情报");
    private String desc;

    Function(String desc) {
        this.desc = desc;
    }


    public String getDesc() {
        return desc;
    }

}
