package com.kakarote.module.common.expression.func.logical;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.util.Map;

/**
 * @description: IF
 * @author: zjj
 * @date: 2021-05-07 16:57
 */
public class IfFunc extends AbstractFunction implements ICustomFunc {
    @Override
    public String getName() {
        return "IF";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        Boolean expressionValue = FunctionUtils.getBooleanValue(arg1, env);
        if (expressionValue) {
            if (arg2 instanceof AviatorNumber) {
                return AviatorNumber.valueOf(FunctionUtils.getNumberValue(arg2, env));
            } else if (arg2 instanceof AviatorString) {
                return new AviatorString(FunctionUtils.getStringValue(arg2, env));
            } else if (arg2 instanceof AviatorBoolean) {
                return AviatorBoolean.valueOf(FunctionUtils.getBooleanValue(arg2, env));
            } else {
                return new AviatorString(FunctionUtils.getJavaObject(arg2, env).toString());
            }
        } else {
            if (arg3 instanceof AviatorNumber) {
                return AviatorNumber.valueOf(FunctionUtils.getNumberValue(arg3, env));
            } else if (arg3 instanceof AviatorString) {
                return new AviatorString(FunctionUtils.getStringValue(arg3, env));
            } else if (arg3 instanceof AviatorBoolean) {
                return AviatorBoolean.valueOf(FunctionUtils.getBooleanValue(arg3, env));
            } else {
                return new AviatorString(FunctionUtils.getJavaObject(arg3, env).toString());
            }
        }
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.LOGICAL;
    }
}
