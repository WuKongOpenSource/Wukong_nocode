package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.Category;
import com.kakarote.module.mapper.CategoryMapper;
import com.kakarote.module.service.ICategoryService;
import org.springframework.stereotype.Service;

/**
 * @author zjj
 * @description: CategoryServiceImpl
 * @date 2022/6/9
 */
@Service
public class CategoryServiceImpl extends BaseServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Override
    public Category getByType(Integer type) {
        return lambdaQuery().eq(Category::getType, type).one();
    }
}
