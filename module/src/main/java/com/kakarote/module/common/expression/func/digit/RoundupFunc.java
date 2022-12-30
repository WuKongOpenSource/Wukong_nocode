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
 * @title: RoundupFunc
 * @description: ROUNDUP
 * @date 2022/3/8 15:53
 */
public class RoundupFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "ROUNDUP";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        try {
            Number arg1Str = ObjectUtil.isNotEmpty(arg1.getValue(env)) ? FunctionUtils.getNumberValue(arg1, env) : 0;
            Number arg2Str = ObjectUtil.isNotEmpty(arg2.getValue(env)) ? FunctionUtils.getNumberValue(arg2, env) : 0;
            BigDecimal decimal = BigDecimal.valueOf(arg1Str.doubleValue()).setScale(arg2Str.intValue(), BigDecimal.ROUND_UP);
            decimal = new BigDecimal(decimal.toPlainString());
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
