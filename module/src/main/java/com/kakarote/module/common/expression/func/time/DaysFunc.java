package com.kakarote.module.common.expression.func.time;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
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
 * @title: DaysFunc
 * @description: DAYS
 * @date 2022/3/8 10:27
 */
public class DaysFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "DAYS";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TIME;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        try {
            String arg1Str = FunctionUtils.getStringValue(arg1, env);
            String arg2Str = FunctionUtils.getStringValue(arg2, env);
            DateTime date1 = DateUtil.parseDate(arg1Str);
            DateTime date2 = DateUtil.parseDate(arg2Str);
            long days = DateUtil.between(date1, date2, DateUnit.DAY);
            return AviatorNumber.valueOf(BigDecimal.valueOf(days).setScale(0));
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        try {
            String arg1Str = FunctionUtils.getStringValue(arg1, env);
            String arg2Str = FunctionUtils.getStringValue(arg2, env);
            Boolean isAbs = FunctionUtils.getBooleanValue(arg3, env);
            DateTime date1 = DateUtil.parseDate(arg1Str);
            DateTime date2 = DateUtil.parseDate(arg2Str);
            long days = DateUtil.between(date1, date2, DateUnit.DAY, isAbs);
            return AviatorNumber.valueOf(BigDecimal.valueOf(days).setScale(0));
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }
}
