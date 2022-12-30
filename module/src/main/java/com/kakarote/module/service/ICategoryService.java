package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.Category;

/**
 * @author zjj
 * @description: ICategoryService
 * @date 2022/6/9
 */
public interface ICategoryService extends BaseService<Category> {

    /**
     * 根据分类获取分类
     *
     * @param type
     * @return
     */
    Category getByType(Integer type);
}
