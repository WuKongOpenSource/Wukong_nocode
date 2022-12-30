package com.kakarote.module.common.expression.func.digit;


import com.googlecode.aviator.runtime.function.system.MinFunction;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;



/**
 * @author zjj
 * @title: MinFunc
 * @description: MIN
 * @date 2022/3/8 14:02
 */
public class MinFunc extends MinFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "MIN";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.DIGIT;
    }
}
