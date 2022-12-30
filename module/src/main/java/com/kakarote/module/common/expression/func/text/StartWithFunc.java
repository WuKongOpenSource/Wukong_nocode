package com.kakarote.module.common.expression.func.text;

import cn.hutool.core.util.StrUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;
import com.kakarote.module.constant.ModuleCodeEnum;

import java.util.Map;

/**
 * @author zjj
 * @title: StartWithFunc
 * @description: STARTWITH
 * @date 2022/3/7 19:56
 */
public class StartWithFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "STARTWITH";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        try {
            String arg1Str = FunctionUtils.getStringValue(arg1, env);
            String arg2Str = FunctionUtils.getStringValue(arg2, env);
            if (StrUtil.isNotEmpty(arg1Str) && StrUtil.isNotEmpty(arg2Str)) {
                if (arg1Str.startsWith(arg2Str)) {
                    return AviatorBoolean.TRUE;
                }
            }
        }catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
        return AviatorBoolean.FALSE;
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TEXT;
    }
}
