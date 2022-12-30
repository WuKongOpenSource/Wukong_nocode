package com.kakarote.module.constant;

/**
 * @author zjj
 * @title: ModuleType
 * @description: 模块类型
 * @date 2022/7/5 17:09
 */
public enum ModuleType {
    MODULE(1),
    BI(2),
    INNER_MODULE(3)
    ;

    private Integer type;

    public Integer getType() {
        return type;
    }

    ModuleType(Integer type) {
        this.type = type;
    }
}
