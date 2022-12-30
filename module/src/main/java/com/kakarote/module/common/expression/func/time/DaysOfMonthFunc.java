package com.kakarote.module.common.expression.func.time;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBigInt;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;
import com.kakarote.module.constant.ModuleCodeEnum;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Map;

/**
 * DayOfMonthFunc
 *
 * @author : zjj
 * @since : 2022/9/14
 */
public class DaysOfMonthFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TIME;
    }

    @Override
    public String getName() {
        return "DAYSOFMONTH";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        try {
            String arg1Str = FunctionUtils.getStringValue(arg1, env);
            DateTime date = DateTime.parse(arg1Str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date.toDate());
            int daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            return new AviatorBigInt(daysOfMonth);
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }
}
