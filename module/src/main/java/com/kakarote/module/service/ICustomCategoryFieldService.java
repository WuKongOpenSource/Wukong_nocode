package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.CustomCategoryField;

import java.util.List;

/**
 * @author zjj
 * @title: ICustomCategoryFieldService
 * @description: 自定义模块分类字段服务接口
 * @date 2022/3/29 15:22
 */
public interface ICustomCategoryFieldService extends BaseService<CustomCategoryField> {

    /**
     *  根据模块id和版本号获取自定义模块分类字段配置
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<CustomCategoryField> getByModuleIdAndVersion(Long moduleId, Integer version);

    /**
     * 根据分类id和版本号获取自定义模块分类字段配置
     *
     * @param categoryId
     * @param version
     * @return
     */
    List<CustomCategoryField> getByCategoryIdAndVersion(Long categoryId, Integer version);
}
