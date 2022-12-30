package com.kakarote.module.common.expression.func.digit;

import com.googlecode.aviator.runtime.function.system.MaxFunction;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

/**
 * @author zjj
 * @title: MaxFunc
 * @description: MAX
 * @date 2022/3/8 14:11
 */
public class MaxFunc extends MaxFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "MAX";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.DIGIT;
    }
}
