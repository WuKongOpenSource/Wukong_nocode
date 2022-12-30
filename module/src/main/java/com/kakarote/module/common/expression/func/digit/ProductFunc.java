package com.kakarote.module.common.expression.func.digit;

import cn.hutool.core.util.ObjectUtil;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: ProductFunc
 * @description: PRODUCT
 * @date 2022/3/8 15:19
 */
public class ProductFunc extends AbstractVariadicFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "PRODUCT";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        List<BigDecimal> numbers = Arrays.stream(args)
                .map(a -> BigDecimal.valueOf(
                        (ObjectUtil.isNotEmpty(a.getValue(env)) ? FunctionUtils.getNumberValue(a, env).doubleValue() : 0)
                ))
                .collect(Collectors.toList());
        BigDecimal result = numbers.stream().reduce(BigDecimal::multiply).orElse(BigDecimal.ZERO);
        return AviatorNumber.valueOf(result);
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.DIGIT;
    }
}
