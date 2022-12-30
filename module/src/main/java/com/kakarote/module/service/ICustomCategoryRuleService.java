package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.CustomCategoryRule;

import java.util.List;

/**
 * @author zjj
 * @title: ICustomCategoryRuleService
 * @description: 自定义模块分类规则服务接口
 * @date 2022/3/29 15:23
 */
public interface ICustomCategoryRuleService extends BaseService<CustomCategoryRule> {

    /**
     * 根据模块id和版本号获取自定义模块分类规则配置
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<CustomCategoryRule> getByModuleIdAndVersion(Long moduleId, Integer version);
}
