package com.kakarote.module.common.expression.func.digit;

import cn.hutool.core.util.ObjectUtil;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.function.math.MathSqrtFunction;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author zjj
 * @title: SqrtFunc
 * @description: SQRT
 * @date 2022/3/8 15:37
 */
public class SqrtFunc extends MathSqrtFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "SQRT";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Number num = ObjectUtil.isNotEmpty(arg1.getValue(env)) ? FunctionUtils.getNumberValue(arg1, env) : 0;
        BigDecimal decimal = BigDecimal.valueOf(Math.sqrt(num.doubleValue())).setScale(14, BigDecimal.ROUND_HALF_UP);
        return AviatorNumber.valueOf(decimal);
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.DIGIT;
    }
}
