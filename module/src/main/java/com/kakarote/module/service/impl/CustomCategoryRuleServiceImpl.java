package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.CustomCategoryRule;
import com.kakarote.module.mapper.CustomCategoryRuleMapper;
import com.kakarote.module.service.ICustomCategoryRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zjj
 * @title: CustomCategoryRuleServiceImpl
 * @description: 自定义模块分类规则服务实现
 * @date 2022/3/29 15:26
 */
@Service
public class CustomCategoryRuleServiceImpl extends BaseServiceImpl<CustomCategoryRuleMapper, CustomCategoryRule> implements ICustomCategoryRuleService {

    @Override
    public List<CustomCategoryRule> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(CustomCategoryRule::getModuleId, moduleId)
                .eq(CustomCategoryRule::getVersion, version)
                .list();
    }
}
