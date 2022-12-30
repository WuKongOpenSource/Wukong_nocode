package com.kakarote.module.common.expression;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;

/**
 * @author zjj
 * @title: ExpressionTypeEnum
 * @description: 表达式分类
 * @date 2022/3/7 15:49
 */
public enum ExpressionCategoryEnum {
    LOGICAL(1, "逻辑函数"),
    TEXT(2, "文本函数"),
    TIME(3, "时间函数"),
    DIGIT(4, "数字函数"),

    NULL(999, "NULL"),
    ;

    ExpressionCategoryEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private Integer type;
    private String desc;

    public Integer getType() {
        return type;
    }


    public static ExpressionCategoryEnum parse(Integer type) {
        return Arrays.stream(ExpressionCategoryEnum.values())
                .filter(e -> ObjectUtil.equal(type, e.getType()))
                .findFirst().orElse(NULL);
    }
}
