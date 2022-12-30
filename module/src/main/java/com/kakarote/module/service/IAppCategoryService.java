package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.AppCategoryBO;
import com.kakarote.module.entity.BO.AppCategorySaveBO;
import com.kakarote.module.entity.BO.AppStoreBO;
import com.kakarote.module.entity.PO.AppCategory;

import java.util.List;

/**
 * @author zjj
 * @description: IAppCategoryService
 * @date 2022/6/9
 */
public interface IAppCategoryService extends BaseService<AppCategory> {

    /**
     * 应用收藏
     *
     * @param storeBO
     */
    void storeApp(AppStoreBO storeBO);

    /**
     * 添加应用到自定义分类
     *
     * @param saveBO
     */
    void saveAppCustomCategory(AppCategorySaveBO saveBO);

    /**
     * 获取最后一个应用分类关系
     *
     * @param categoryId
     * @return
     */
    AppCategory getLastAppCategory(Long categoryId);

    /**
     * 获取所有的应用分类关系
     *
     * @return
     */
    List<AppCategoryBO> getAll();

}
