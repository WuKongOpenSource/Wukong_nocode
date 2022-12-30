package com.kakarote.module.constant;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;

/**
 * @author zjj
 * @title: FormulaType
 * @description: 表达式类型
 * @date 2022/3/4 16:48
 */
public enum ExpressionType {
    DIGIT(1, "数字"),
    FLOAT(2, "货币"),
    PERCENTAGE(3, "百分比"),
    DATE(4, "日期"),
    DATE_TIME(5, "日期时间"),
    TEXT(6, "文本"),
    BOOL(7, " 布尔值"),
    NULL(999, "NULL"),
    ;


    ExpressionType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private Integer type;
    private String desc;

    public Integer getType() {
        return type;
    }


    public static ExpressionType parse(Integer type) {
        return Arrays.stream(ExpressionType.values())
                .filter(e -> ObjectUtil.equal(type, e.getType()))
                .findFirst().orElse(NULL);
    }
}
