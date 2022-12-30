package com.kakarote.module.common.expression.func.logical;

import cn.hutool.core.util.ObjectUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.util.Map;

public class NotEmptyFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "NOTEMPTY";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Object object = getAviatorObject(arg1, env);
        if (ObjectUtil.isNotEmpty(object)) {
            return AviatorBoolean.TRUE;
        }
        return AviatorBoolean.FALSE;
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.LOGICAL;
    }
}
