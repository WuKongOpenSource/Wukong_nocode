package com.kakarote.module.common.expression.func.logical;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.util.Map;

/**
 * @author zjj
 * @title: IsNumberFunc
 * @description: ISNUMBER
 * @date 2022/3/7 15:34
 */
public class IsNumberFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "ISNUMBER";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        if (arg1 instanceof AviatorNumber) {
            return AviatorBoolean.TRUE;
        } else if (arg1 instanceof AviatorJavaType) {
            Object object = FunctionUtils.getJavaObject(arg1, env);
            if (object instanceof Number) {
                return AviatorBoolean.TRUE;
            }
        }
        return AviatorBoolean.FALSE;
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.LOGICAL;
    }
}
