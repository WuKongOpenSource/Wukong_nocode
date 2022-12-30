package com.kakarote.module.constant;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;

/**
 * @author zjj
 * @title: ButtonExecuteTypeEnum
 * @description: 自定义按钮执行类型
 * @date 2022/3/19 14:55
 */
public enum ButtonExecuteTypeEnum {
    NOW(0, "立即"),
    CONFIRM(1, "确认"),
    FILL_DATA(2, "填写内容"),
    ;

    private int code;
    private String msg;

    ButtonExecuteTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public static ButtonExecuteTypeEnum parse(Integer code) {
        return Arrays.stream(ButtonExecuteTypeEnum.values())
                .filter(e -> ObjectUtil.equal(code, e.getCode()))
                .findFirst().orElse(null);
    }
}
