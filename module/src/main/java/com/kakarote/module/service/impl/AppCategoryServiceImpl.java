package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.BO.AppCategoryBO;
import com.kakarote.module.entity.BO.AppCategorySaveBO;
import com.kakarote.module.entity.BO.AppStoreBO;
import com.kakarote.module.entity.PO.AppCategory;
import com.kakarote.module.entity.PO.Category;
import com.kakarote.module.mapper.AppCategoryMapper;
import com.kakarote.module.service.IAppCategoryService;
import com.kakarote.module.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @description: AppCategoryServiceImpl
 * @date 2022/6/9
 */
@Service
public class AppCategoryServiceImpl extends BaseServiceImpl<AppCategoryMapper, AppCategory> implements IAppCategoryService {

    @Autowired
    private ICategoryService categoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void storeApp(AppStoreBO storeBO) {
        Category store = categoryService.getByType(1);
        if (ObjectUtil.isNull(store)) {
            // 收藏分类
            store = new Category();
            store.setName("我的收藏");
            store.setType(1);
            store.setSort(0);
            store.setParentId(0L);
            store.setCreateTime(DateUtil.date());
            categoryService.save(store);
        }
        if (storeBO.getIsCancel()) {
            lambdaUpdate()
                    .eq(AppCategory::getCategoryId, store.getCategoryId())
                    .eq(AppCategory::getApplicationId, storeBO.getApplicationId())
                    .remove();
        } else {
            AppCategory lastAppCategory = getLastAppCategory(store.getCategoryId());
            AppCategory appCategory = lambdaQuery()
                    .eq(AppCategory::getCategoryId, store.getCategoryId())
                    .eq(AppCategory::getApplicationId, storeBO.getApplicationId())
                    .one();
            if (ObjectUtil.isNull(appCategory)) {
                appCategory = new AppCategory();
                appCategory.setCategoryId(store.getCategoryId());
                appCategory.setApplicationId(storeBO.getApplicationId());
                appCategory.setSort(Optional.ofNullable(lastAppCategory).map(a -> a.getSort() + 1).orElse(0));
                appCategory.setCreateTime(DateUtil.date());
                save(appCategory);
            }
        }
    }

    @Override
    public void saveAppCustomCategory(AppCategorySaveBO saveBO) {
        // 获取子自定义分类
        Category customCategory = categoryService.getByType(0);
        if (ObjectUtil.isNull(customCategory)) {
            // 自定义应用
            customCategory = new Category();
            customCategory.setName("自定义应用");
            customCategory.setType(0);
            customCategory.setSort(0);
            customCategory.setParentId(0L);
            customCategory.setCreateTime(DateUtil.date());
            categoryService.save(customCategory);
        }
        AppCategory appCategory = lambdaQuery()
                .eq(AppCategory::getCategoryId, customCategory.getCategoryId())
                .eq(AppCategory::getApplicationId, saveBO.getApplicationId())
                .one();
        if (ObjectUtil.isNull(appCategory)) {
            AppCategory lastAppCategory = getLastAppCategory(customCategory.getCategoryId());
            appCategory = new AppCategory();
            appCategory.setCategoryId(customCategory.getCategoryId());
            appCategory.setApplicationId(saveBO.getApplicationId());
            appCategory.setSort(Optional.ofNullable(lastAppCategory).map(a -> a.getSort() + 1).orElse(0));
            appCategory.setCreateTime(DateUtil.date());
            save(appCategory);
        }
    }

    @Override
    public AppCategory getLastAppCategory(Long categoryId) {
        return lambdaQuery()
                .eq(AppCategory::getCategoryId, categoryId)
                .orderByDesc(AppCategory::getSort).one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AppCategoryBO> getAll() {
        List<AppCategoryBO> result = new ArrayList<>();
        List<Category> categories = categoryService.lambdaQuery()
                .eq(Category::getIsSystem, false)
                .eq(Category::getStatus, 1)
                .list();
        if (CollUtil.isEmpty(categories)) {
            // 收藏分类
            Category storeCategory = new Category();
            storeCategory.setName("我的收藏");
            storeCategory.setType(1);
            storeCategory.setSort(0);
            storeCategory.setParentId(0L);
            storeCategory.setIsSystem(false);
            storeCategory.setCreateTime(DateUtil.date());
            categoryService.save(storeCategory);
            // 自定义应用
            Category otherCategory = new Category();
            otherCategory.setName("自定义应用");
            otherCategory.setType(0);
            otherCategory.setSort(0);
            otherCategory.setParentId(0L);
            otherCategory.setIsSystem(false);
            otherCategory.setCreateTime(DateUtil.date());
            categoryService.save(otherCategory);

            categories.add(storeCategory);
            categories.add(otherCategory);
        }
        List<AppCategory> appCategories = lambdaQuery().list();

        Map<Long, List<AppCategory>> categoryIdAppListMap = appCategories.stream()
                .collect(Collectors.groupingBy(AppCategory::getCategoryId));
        for (Category category : categories) {
            AppCategoryBO categoryBO = BeanUtil.copyProperties(category, AppCategoryBO.class);
            List<AppCategory> apps = categoryIdAppListMap.get(category.getCategoryId());
            if (CollUtil.isNotEmpty(apps)) {
                List<String> appIds = apps.stream().sorted(Comparator.comparing(AppCategory::getSort))
                        .map(AppCategory::getApplicationId)
                        .collect(Collectors.toList());
                categoryBO.setApplicationIds(appIds);
            }
            result.add(categoryBO);
        }
        return result;
    }
}
