package com.kakarote.module.constant;


import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.SystemCodeEnum;

import java.util.Objects;

/**
 * 流程类型枚举
 *
 * @date 2020年11月17日
 */
public enum FlowTypeEnum {
    /**
     * 条件节点
     */
    CONDITION(0),
    /**
     * 审批节点
     */
    EXAMINE(1),
    /**
     * 填写节点
     */
    FILL(2),
    /**
     * 抄送节点
     */
    COPY(3),
    /**
     * 添加数据
     */
    SAVE(4),
    /**
     * 更新数据
     */
    UPDATE(5),
    /**
     * 发起人
     */
    START(6);

    FlowTypeEnum(Integer type) {
        this.type = type;
    }

    private Integer type;

    public Integer getType() {
        return type;
    }

    public static FlowTypeEnum parse(Integer type) {
        for (FlowTypeEnum flowTypeEnum : values()) {
            if (Objects.equals(type, flowTypeEnum.getType())) {
                return flowTypeEnum;
            }
        }
        throw new BusinessException(SystemCodeEnum.SYSTEM_NO_VALID);
    }

    public String getServiceName() {
        return name().toLowerCase() + "Service";
    }
}
