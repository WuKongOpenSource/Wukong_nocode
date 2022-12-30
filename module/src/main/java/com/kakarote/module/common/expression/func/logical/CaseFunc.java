package com.kakarote.module.common.expression.func.logical;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.module.common.expression.ExpressionCategoryEnum;
import com.kakarote.module.common.expression.func.ICustomFunc;
import com.kakarote.module.constant.ModuleCodeEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zjj
 * @title: CaseFunc
 * @description: CASE
 * @date 2022/3/7 15:58
 */
public class CaseFunc extends AbstractVariadicFunction implements ICustomFunc {

    @Override
    public String getName() {
        return "CASE";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> map, AviatorObject... aviatorObjects) {
        // 参数的长度
        Integer length = aviatorObjects.length;
        if (length < 4 || length % 2 != 0) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
        List<AviatorObject> aviatorObjectList = Arrays.asList(aviatorObjects);
        AviatorObject firstArg = CollUtil.getFirst(aviatorObjectList);
        AviatorObject lastArg = CollUtil.getLast(aviatorObjectList);
        AtomicInteger atomicInteger = new AtomicInteger(1);
        // 计算需要遍历的次数
        int count = (length - 2) / 2;
        Object object = getAviatorObject(firstArg, map);
        for (Integer i = 0; i < count; i++) {
            int index = atomicInteger.getAndAdd(2);
            AviatorObject targetArg = CollUtil.get(aviatorObjectList, index);
            Object target = getAviatorObject(targetArg, map);
            if (ObjectUtil.isNull(object)) {
                if (ObjectUtil.isNull(target)) {
                    return CollUtil.get(aviatorObjectList, index + 1);
                }
            } else {
                if (ObjectUtil.isNotNull(target)) {
                    if (StrUtil.equals(object.toString(), target.toString())) {
                        return CollUtil.get(aviatorObjectList, index + 1);
                    }
                }
            }

        }
        return lastArg;
    }

    @Override
    public ExpressionCategoryEnum getType() {
        return ExpressionCategoryEnum.LOGICAL;
    }


}
