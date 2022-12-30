package com.kakarote.module.common.expression.func.digit;

import cn.hutool.core.util.ObjectUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;
import com.kakarote.module.constant.ModuleCodeEnum;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author zjj
 * @title: IntFunc
 * @description: INT
 * @date 2022/4/11 10:37
 */
public class IntFunc extends AbstractFunction implements ICustomFunc {


    @Override
    public String getName() {
        return "INT";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        try {
            Number arg1Str = ObjectUtil.isNotEmpty(arg1.getValue(env)) ? FunctionUtils.getNumberValue(arg1, env) : 0;
            BigDecimal decimal = BigDecimal.valueOf(arg1Str.doubleValue()).setScale(0, BigDecimal.ROUND_DOWN);
            return AviatorNumber.valueOf(decimal);
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.DIGIT;
    }
}
