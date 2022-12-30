package com.kakarote.module.constant;

import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.SystemCodeEnum;

import java.util.Objects;

/**
 * 审批类型枚举
 *
 * @date 2020年11月17日
 */
public enum FlowExamineTypeEnum {
    /**
     * 指定成员审批
     */
    MEMBER(1),
    /**
     * 负责人主管审批
     */
    SUPERIOR(2),
    /**
     * 指定角色审批
     */
    ROLE(3),
    /**
     * 发起人自选成员审批
     */
    OPTIONAL(4),
    /**
     * 连续多级主管审批
     */
    CONTINUOUS_SUPERIOR(5),

    /**
     * 管理员审批，只在找不到审批对象时存在
     */
    MANAGER(6);

    FlowExamineTypeEnum(Integer type) {
        this.type = type;
    }

    private Integer type;

    public Integer getType() {
        return type;
    }

    public static FlowExamineTypeEnum parse(Integer type) {
        for (FlowExamineTypeEnum examineTypeEnum : values()) {
            if (Objects.equals(type, examineTypeEnum.getType())) {
                return examineTypeEnum;
            }
        }
        throw new BusinessException(SystemCodeEnum.SYSTEM_NO_VALID);
    }
}
