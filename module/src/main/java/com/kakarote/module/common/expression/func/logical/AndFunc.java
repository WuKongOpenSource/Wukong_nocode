package com.kakarote.module.common.expression.func.logical;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.util.Map;

/**
 * @description: AND
 * @author: zjj
 * @date: 2021-05-07 17:51
 */
public class AndFunc extends AbstractVariadicFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "AND";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> map, AviatorObject... aviatorObjects) {
        for (AviatorObject arg : aviatorObjects) {
            if (!FunctionUtils.getBooleanValue(arg, map)) {
                return AviatorBoolean.FALSE;
            }
        }
        return AviatorBoolean.TRUE;
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.LOGICAL;
    }
}
