package com.kakarote.module.common.expression.func.digit;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author zjj
 * @title: CountFunc
 * @description: COUNT
 * @date 2022/3/8 15:19
 */
public class CountFunc extends AbstractVariadicFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "COUNT";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> map, AviatorObject... aviatorObjects) {
        int length = aviatorObjects.length;
        return AviatorNumber.valueOf(BigDecimal.valueOf(length));
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.DIGIT;
    }

}
