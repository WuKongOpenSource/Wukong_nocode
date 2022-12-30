package com.kakarote.module.common.expression.func.digit;

import cn.hutool.core.util.ObjectUtil;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: AverageFunc
 * @description: AVERAGE
 * @date 2022/3/8 15:22
 */
public class AverageFunc extends AbstractVariadicFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "AVERAGE";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> map, AviatorObject... aviatorObjects) {
        int length = aviatorObjects.length;
        List<Number> numbers = Arrays.stream(aviatorObjects)
                .map(a -> ObjectUtil.isNotEmpty(a.getValue(map)) ? FunctionUtils.getNumberValue(a, map): 0)
                .collect(Collectors.toList());
        List<BigDecimal> decimals = numbers.stream().map(n -> BigDecimal.valueOf(n.doubleValue())).collect(Collectors.toList());
        BigDecimal average = decimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(length), 14, RoundingMode.HALF_UP);
        return AviatorNumber.valueOf(average);
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.DIGIT;
    }
}
