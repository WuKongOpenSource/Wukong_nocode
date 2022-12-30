package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.CustomCategory;

import java.util.List;

/**
 * @author zjj
 * @title: ICustomCategoryService
 * @description: 自定义模块分类服务接口
 * @date 2022/3/29 15:22
 */
public interface ICustomCategoryService extends BaseService<CustomCategory> {

    /**
     * 获取模块的自定义模块分类
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<CustomCategory> getByModuleIdAndVersion(Long moduleId, Integer version);

    /**
     * 获取指定的自定义模块分类
     *
     * @param categoryId
     * @param version
     * @return
     */
    CustomCategory getByCategoryId(Long categoryId, Integer version);

    /**
     * 处理数据分组
     *
     * @param moduleId
     */
    void dealDataCategory(Long moduleId);

    /**
     * 获取所有分类
     *
     * @return
     */
    List<CustomCategory> getAll();
}
