package com.kakarote.module.common.expression.func.time;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
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
 * @title: AddDayFunc
 * @description: ADDDAY
 * @date 2022/3/8 11:26
 */
public class AddDayFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "ADDDAY";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TIME;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        try {
            String arg1Str = FunctionUtils.getStringValue(arg1, env);
            Number arg2Str = FunctionUtils.getNumberValue(arg2, env);
            if (StrUtil.isEmpty(arg1Str)) {
                return null;
            }
            DateTime date = DateUtil.parse(arg1Str);
            date = DateUtil.offsetDay(date, arg2Str.intValue());
            return new AviatorString(DateUtil.formatDateTime(date));
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }
}
