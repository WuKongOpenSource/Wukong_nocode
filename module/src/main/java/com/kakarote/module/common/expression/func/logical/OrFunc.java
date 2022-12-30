package com.kakarote.module.common.expression.func.logical;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.util.Map;

/**
 * @description: OR
 * @author: zjj
 * @date: 2021-05-07 18:34
 */
public class OrFunc extends AbstractVariadicFunction implements ICustomFunc {

	@Override
	public String getName() {
		return "OR";
	}

    @Override
    public AviatorObject variadicCall(Map<String, Object> map, AviatorObject... aviatorObjects) {
        for (AviatorObject arg : aviatorObjects) {
            if (FunctionUtils.getBooleanValue(arg, map)) {
                return AviatorBoolean.TRUE;
            }
        }
        return AviatorBoolean.FALSE;
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.LOGICAL;
    }
}
