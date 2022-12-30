package com.kakarote.module.common.expression.func.text;

import cn.hutool.core.util.ObjectUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;
import com.kakarote.module.common.expression.func.util.Digit2CN;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author zjj
 * @title: RMBUpperFunc
 * @description: RMBUPPER
 * @date 2022/3/8 16:01
 */
public class RMBUpperFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "RMBUPPER";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Number arg1Str = ObjectUtil.isNotEmpty(arg1.getValue(env)) ? FunctionUtils.getNumberValue(arg1, env) : 0;
        String result = Digit2CN.getStr(BigDecimal.valueOf(arg1Str.doubleValue()).toPlainString());
        return new AviatorString(result);
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TEXT;
    }
}
