package com.kakarote.module.common.expression.func.digit;

import cn.hutool.core.collection.CollUtil;
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
 * @title: SubtractsFunc
 * @description: SUBTRACTS
 * @date 2022/3/8 15:16
 */
public class SubtractsFunc extends AbstractVariadicFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "SUBTRACTS";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.DIGIT;
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        int length = args.length;
        List<AviatorObject> aviatorObjects = Arrays.asList(args);
        AviatorObject arg1 = CollUtil.getFirst(aviatorObjects);
        Number arg1Str = ObjectUtil.isNotEmpty(arg1.getValue(env)) ? FunctionUtils.getNumberValue(arg1, env) : 0;
        BigDecimal decimal1 = BigDecimal.valueOf(arg1Str.doubleValue());
        List<Number> numbers = CollUtil.sub(aviatorObjects, 1, length).stream()
                .map(a -> ObjectUtil.isNotEmpty(a.getValue(env)) ? FunctionUtils.getNumberValue(a, env): 0)
                .collect(Collectors.toList());
        List<BigDecimal> decimals = numbers.stream().map(n -> BigDecimal.valueOf(n.doubleValue())).collect(Collectors.toList());
        BigDecimal sum = decimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal result = decimal1.subtract(sum);
        return AviatorNumber.valueOf(result);
    }
}
