package com.kakarote.module.common.expression.func.text;

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
 * @title: MidFunc
 * @description: MID
 * @date 2022/3/7 20:06
 */
public class MidFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "MID";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TEXT;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        try {
            String arg1Str = FunctionUtils.getStringValue(arg1, env);
            Number fromIndex = FunctionUtils.getNumberValue(arg2, env);
            Number length = FunctionUtils.getNumberValue(arg3, env);
            return new AviatorString(StrUtil.subWithLength(arg1Str, fromIndex.intValue() - 1, length.intValue()));
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }


}
