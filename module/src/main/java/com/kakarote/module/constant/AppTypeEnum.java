package com.kakarote.module.constant;

/**
 * @author zjj
 * @description: AppTypeEnum
 * @date 2022/6/17
 */
public enum AppTypeEnum {

    CUSTOM(0, "自定义"),
    INSTALLED(1, "安装"),
    IMPORTED(2, "导入"),
    ;

    private int code;
    private String msg;

    AppTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
