package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.expression.ExpressionUtil;
import com.kakarote.module.entity.BO.ModuleFormulaBO;
import com.kakarote.module.entity.BO.SearchBO;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.mapper.CustomCategoryMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: CustomCategoryServiceImpl
 * @description: 自定义模块分类服务实现
 * @date 2022/3/29 15:26
 */
@Service
public class CustomCategoryServiceImpl extends BaseServiceImpl<CustomCategoryMapper, CustomCategory> implements ICustomCategoryService,
        IModuleFormulaService, ModulePageService {

    @Autowired
    private ICustomCategoryRuleService categoryRuleService;

    @Autowired
    private IModuleFieldDataCommonService fieldDataCommonService;

    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(10, 20, 5L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(2048), new ThreadPoolExecutor.AbortPolicy());

    @Override
    public List<CustomCategory> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(CustomCategory::getModuleId, moduleId)
                .eq(CustomCategory::getVersion, version)
                .orderByAsc(CustomCategory::getSort)
                .list();
    }

    @Override
    public CustomCategory getByCategoryId(Long categoryId, Integer version) {
        return lambdaQuery().eq(CustomCategory::getCategoryId, categoryId).eq(CustomCategory::getVersion, version).one();
    }

    @Override
    public void dealDataCategory(Long moduleId) {
        ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
        Integer version = module.getVersion();
        List<CustomCategory> categories = this.getByModuleIdAndVersion(moduleId, version);
        List<CustomCategoryRule> ruleList = categoryRuleService.getByModuleIdAndVersion(moduleId, version);
        if (ruleList.isEmpty()) {
            return;
        }
        Map<Long, List<CustomCategoryRule>> rulesGroupByCategoryId = ruleList.stream().collect(Collectors.groupingBy(CustomCategoryRule::getCategoryId));

        List<ModuleField> moduleFields = ApplicationContextHolder.getBean(IModuleFieldService.class).getByModuleId(moduleId, null);

        UserInfo user = UserCacheUtil.getUserInfo(module.getCreateUserId());
        THREAD_POOL.execute(() -> {
            try {
                UserUtil.setUser(user);
                AtomicInteger page = new AtomicInteger(1);
                setDataCategory(moduleId, page, categories, rulesGroupByCategoryId, moduleFields);
            } finally {
                UserUtil.removeUser();
            }
        });
    }

    private void setDataCategory(Long moduleId,
                                 AtomicInteger page,
                                 List<CustomCategory> categories,
                                 Map<Long, List<CustomCategoryRule>> rulesGroupByCategoryId,
                                 List<ModuleField> moduleFields) {
        SearchBO searchBO = new SearchBO();
        searchBO.setPage(page.getAndIncrement());
        searchBO.setLimit(2000);
        searchBO.setModuleId(moduleId);
        searchBO.setAuthFilter(false);
        BasePage<Map<String, Object>> data = queryPageList(searchBO, moduleId);
        CustomCategory defaultCategory = categories.stream().filter(c -> ObjectUtil.equal(0, c.getType())).findFirst().orElse(null);
        if (ObjectUtil.isNull(defaultCategory)) {
            return;
        }
        Set<Long> dataIds = new HashSet<>();
        for (CustomCategory category : categories) {
            List<CustomCategoryRule> rules = rulesGroupByCategoryId.get(category.getCategoryId());
            if (CollUtil.isEmpty(rules)) {
                continue;
            }
            for (CustomCategoryRule rule : rules) {
                ModuleFormulaBO formulaBO = JSON.parseObject(rule.getFormula(), ModuleFormulaBO.class);
                for (Map<String, Object> map : data.getList()) {
                    Long dataId = MapUtil.getLong(map, "dataId");
                    Long categoryId = MapUtil.getLong(map, "categoryId");
                    if (CollUtil.contains(dataIds, dataId)) {
                        continue;
                    }
                    // 从默认分类转出
                    if (ObjectUtil.equal(defaultCategory.getCategoryId(), rule.getFrom())) {
                        if (ObjectUtil.isNotNull(categoryId)) {
                            continue;
                        }
                    } else {
                        if (ObjectUtil.notEqual(rule.getFrom(), categoryId)) {
                            continue;
                        }
                    }
                    Map<String, Object> env = buildFormulaEnv(moduleFields, map);
                    formulaBO.setEnv(env);
                    try {
                        if (ObjectUtil.equal(AviatorBoolean.TRUE, ExpressionUtil.execute(formulaBO)) || ObjectUtil.equal(true, ExpressionUtil.execute(formulaBO))) {
                            dataIds.add(dataId);
                            ModuleFieldDataCommon dataCommon = fieldDataCommonService.getByDataId(dataId);
                            // 转移到默认分类
                            if (ObjectUtil.equal(defaultCategory.getCategoryId(), rule.getTo())) {
                                dataCommon.setCategoryId(null);
                            } else {
                                dataCommon.setCategoryId(rule.getTo());
                            }
                            dataCommon.setUpdateTime(DateUtil.date());
                            fieldDataCommonService.updateById(dataCommon);
                            // 更新ES
                            Map<String, Object> fieldValueMap = new HashMap<>(1);
                            fieldValueMap.put("categoryId", dataCommon.getCategoryId());
                            fieldValueMap.put("updateTime", DateUtil.formatDateTime(dataCommon.getUpdateTime()));
                            updateField(fieldValueMap, dataId, moduleId);
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }
        if (page.get() < data.getPages()) {
            setDataCategory(moduleId, page, categories, rulesGroupByCategoryId, moduleFields);
        }
    }

    @Override
    public List<CustomCategory> getAll() {
        return baseMapper.getALL();
    }
}
