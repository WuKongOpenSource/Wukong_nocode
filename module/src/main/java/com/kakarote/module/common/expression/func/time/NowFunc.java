package com.kakarote.module.common.expression.func.time;

import cn.hutool.core.date.DateUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;

import java.util.Map;

/**
 * @author zjj
 * @title: NowFunc
 * @description: NOW
 * @date 2022/3/8 11:08
 */
public class NowFunc extends AbstractFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "NOW";
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.TIME;
    }

    @Override
    public AviatorObject call(Map<String, Object> env) {
        String now = DateUtil.now();
        return new AviatorString(now);
    }
}
