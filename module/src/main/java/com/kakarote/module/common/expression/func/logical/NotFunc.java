package com.kakarote.module.common.expression.func.logical;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.util.Map;

/**
 * @author zjj
 * @title: NotFunc
 * @description: NOT
 * @date 2022/3/7 13:14
 */
public class NotFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "NOT";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        if (!FunctionUtils.getBooleanValue(arg1, env)) {
            return AviatorBoolean.TRUE;
        }
        return AviatorBoolean.FALSE;
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.LOGICAL;
    }
}
