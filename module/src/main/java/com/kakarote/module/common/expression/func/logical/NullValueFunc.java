package com.kakarote.module.common.expression.func.logical;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.*;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.util.Map;

/**
 * @author zjj
 * @title: NullValueFunc
 * @description: NULLVALUE
 * @date 2022/3/7 17:14
 */
public class NullValueFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "NULLVALUE";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        Boolean isNUll = false;
        if (arg1 instanceof AviatorJavaType) {
            Object object = FunctionUtils.getJavaObject(arg1, env);
            if (ObjectUtil.isEmpty(object)) {
                isNUll = true;
            }
        } else if (arg1 instanceof AviatorString) {
            String value = FunctionUtils.getStringValue(arg1, env);
            if (StrUtil.equals("null", value) || StrUtil.isEmpty(value)) {
                isNUll = true;
            }
        } else if (arg1 instanceof AviatorNil) {
            isNUll = true;
        }
        if (isNUll) {
            if (arg2 instanceof AviatorNumber) {
                return AviatorNumber.valueOf(FunctionUtils.getNumberValue(arg2, env));
            } else if (arg2 instanceof AviatorString) {
                return new AviatorString(FunctionUtils.getStringValue(arg2, env));
            } else if (arg2 instanceof AviatorBoolean) {
                return AviatorBoolean.valueOf(FunctionUtils.getBooleanValue(arg2, env));
            } else {
                return new AviatorString(FunctionUtils.getJavaObject(arg2, env).toString());
            }
        }
        return null;
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.LOGICAL;
    }
}
