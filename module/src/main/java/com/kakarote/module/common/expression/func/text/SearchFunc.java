package com.kakarote.module.common.expression.func.text;

import cn.hutool.core.util.StrUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;
import com.kakarote.module.constant.ModuleCodeEnum;

import java.util.Map;

/**
 * @author zjj
 * @title: SearchFunc
 * @description: SEARCH
 * @date 2022/3/8 9:18
 */
public class SearchFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "SEARCH";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TEXT;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        try {
            String arg1Str = FunctionUtils.getStringValue(arg1, env);
            String arg2Str = FunctionUtils.getStringValue(arg2, env);
            Number fromIndex = FunctionUtils.getNumberValue(arg3, env);

            if (StrUtil.isNotEmpty(arg1Str) && StrUtil.isNotEmpty(arg2Str)) {
                int index = StrUtil.indexOf(arg1Str, arg2Str, fromIndex.intValue() - 1, false) + 1;
                return AviatorNumber.valueOf(index);
            }
        }catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
        return AviatorNumber.valueOf(0);
    }
}
