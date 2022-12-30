package com.kakarote.module.common.expression.func.time;

import cn.hutool.core.date.DateUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;
import com.kakarote.module.constant.ModuleCodeEnum;

import java.util.Calendar;
import java.util.Map;

/**
 * @author zjj
 * @title: DateFunc
 * @description: DATE
 * @date 2022/3/8 10:56
 */
public class DateFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "DATE";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TIME;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        try {
            Number arg1Str = FunctionUtils.getNumberValue(arg1, env);
            Number arg2Str = FunctionUtils.getNumberValue(arg2, env);
            Number arg3Str = FunctionUtils.getNumberValue(arg3, env);
            Calendar cal = Calendar.getInstance();
            cal.set(arg1Str.intValue(), arg2Str.intValue() - 1, arg3Str.intValue(), 0, 0, 0);
            String date = DateUtil.formatDate(cal.getTime());
            return new AviatorString(date);
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }
}
