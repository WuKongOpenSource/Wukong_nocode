package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.CustomCategoryField;
import com.kakarote.module.mapper.CustomCategoryFieldMapper;
import com.kakarote.module.service.ICustomCategoryFieldService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zjj
 * @title: CustomCategoryFieldServiceImpl
 * @description: 自定义模块分类字段服务实现
 * @date 2022/3/29 15:24
 */
@Service
public class CustomCategoryFieldServiceImpl extends BaseServiceImpl<CustomCategoryFieldMapper, CustomCategoryField> implements ICustomCategoryFieldService {

    @Override
    public List<CustomCategoryField> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(CustomCategoryField::getModuleId, moduleId)
                .eq(CustomCategoryField::getVersion, version)
                .list();
    }

    @Override
    public List<CustomCategoryField> getByCategoryIdAndVersion(Long categoryId, Integer version) {
        return lambdaQuery()
                .eq(CustomCategoryField::getCategoryId, categoryId)
                .eq(CustomCategoryField::getVersion, version)
                .list();
    }
}
