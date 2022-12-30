package com.kakarote.module.constant;

/**
 * @author zjj
 * @title: FlowMetadataTypeEnum
 * @description: 流程元数据类型
 * @date 2022/3/17 9:41
 */
public enum FlowMetadataTypeEnum {
    /**
     * 系统
     */
    SYSTEM(0, "system"),
    /**
     * 自定义按钮
     */
    CUSTOM_BUTTON(1, "customButton"),
    ;

    FlowMetadataTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private Integer type;
    private String desc;

    public Integer getType() {
        return type;
    }
}
