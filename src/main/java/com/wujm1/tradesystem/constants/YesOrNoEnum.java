package com.wujm1.tradesystem.constants;

import lombok.Data;

/**
 * @author wujiaming
 * @date 2024-09-02 17:56
 */
public enum YesOrNoEnum {
    YES("yes", "是"),
    NO("no", "否");

    private String code;
    private String desc;

    YesOrNoEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
