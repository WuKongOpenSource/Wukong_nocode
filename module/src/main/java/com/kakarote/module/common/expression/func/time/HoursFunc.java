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
 * @title: HoursFunc
 * @description: HOURS
 * @date 2022/3/8 10:29
 */
public class HoursFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "HOURS";
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
            DateTime date1 = DateUtil.parseDateTime(arg1Str);
            DateTime date2 = DateUtil.parseDateTime(arg2Str);
            long minutes = DateUtil.between(date1, date2, DateUnit.MINUTE);
            BigDecimal m = new BigDecimal(minutes);
            BigDecimal hours = m.divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
            return AviatorNumber.valueOf(hours);
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
            DateTime date1 = DateUtil.parseDateTime(arg1Str);
            DateTime date2 = DateUtil.parseDateTime(arg2Str);
            long minutes = DateUtil.between(date1, date2, DateUnit.MINUTE, isAbs);
            BigDecimal m = new BigDecimal(minutes);
            BigDecimal hours = m.divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
            return AviatorNumber.valueOf(hours);
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }
}
