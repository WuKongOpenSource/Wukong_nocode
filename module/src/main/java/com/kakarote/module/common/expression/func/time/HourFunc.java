package com.kakarote.module.common.expression.func.time;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;
import com.kakarote.module.constant.ModuleCodeEnum;

import java.util.Map;

/**
 * @author zjj
 * @title: HourFunc
 * @description: HOUR
 * @date 2022/3/8 10:03
 */
public class HourFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "HOUR";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TIME;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        try {
            String arg1Str = FunctionUtils.getStringValue(arg1, env);
            DateTime date = DateUtil.parseDateTime(arg1Str);
            String hour = DateUtil.format(date, "HH");
            return new AviatorString(hour);
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }
}
