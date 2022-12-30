package com.kakarote.module.common.expression.func.logical;

import cn.hutool.core.util.ObjectUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.util.Map;

/**
 * @author zjj
 * @title: IsNullFunc
 * @description: ISNULL
 * @date 2022/3/7 15:13
 */
public class IsNullFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "ISNULL";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Object object = getAviatorObject(arg1, env);
        if (ObjectUtil.isNotEmpty(object)) {
            return AviatorBoolean.FALSE;
        }
        return AviatorBoolean.TRUE;
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.LOGICAL;
    }
}
