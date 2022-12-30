package com.kakarote.module.common.expression.func;

import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.*;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;

import java.util.Map;

/**
 * @author zjj
 * @title: ICustomFunc
 * @description: 自定义抽象工具类
 * @date 2022/3/4 20:25
 */
public interface ICustomFunc extends AviatorFunction {

    ExpressionCategoryEnum getType();

    default Object getAviatorObject(AviatorObject arg, Map<String, Object> env) {
        if (arg instanceof AviatorNumber) {
            return FunctionUtils.getNumberValue(arg, env);
        } else if (arg instanceof AviatorString) {
            return FunctionUtils.getStringValue(arg, env);
        } else if (arg instanceof AviatorBoolean) {
            return FunctionUtils.getBooleanValue(arg, env);
        } else if (arg instanceof AviatorNil) {
            return null;
        } else {
            return FunctionUtils.getJavaObject(arg, env);
        }
    }

}
