package com.kakarote.module.common.expression.func.digit;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: SumFunc
 * @description: SUM
 * @date 2022/3/8 14:51
 */
public class SumFunc extends AbstractVariadicFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "SUM";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> map, AviatorObject... aviatorObjects) {
        List<Number> numbers = new ArrayList<>();
        for (AviatorObject aviatorObject : Arrays.stream(aviatorObjects).collect(Collectors.toList())) {
            if (ObjectUtil.isNotEmpty(aviatorObject.getValue(map))) {
                if (aviatorObject.getValue(map) instanceof List<?>) {
                    List<BigDecimal> bigDecimals = JSON.parseArray(aviatorObject.getValue(map).toString(), BigDecimal.class);
                    BigDecimal decimal = bigDecimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                    numbers.add(decimal);
                } else {
                    numbers.add(FunctionUtils.getNumberValue(aviatorObject, map));
                }
            } else {
                numbers.add(0);
            }
        }
        List<BigDecimal> decimals = numbers.stream().map(n -> BigDecimal.valueOf(n.doubleValue())).collect(Collectors.toList());
        BigDecimal sum = decimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return AviatorNumber.valueOf(sum);
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.DIGIT;
    }
}
