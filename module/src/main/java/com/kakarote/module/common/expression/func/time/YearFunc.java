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
 * @title: YearFunc
 * @description: YEAR
 * @date 2022/3/8 9:31
 */
public class YearFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "YEAR";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TIME;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        try {
            String arg1Str = FunctionUtils.getStringValue(arg1, env);
            DateTime date = DateUtil.parseDate(arg1Str);
            String year = DateUtil.format(date, "yyyy");
            return new AviatorString(year);
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }
}
