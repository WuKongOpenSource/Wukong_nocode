package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.ModuleFieldCacheUtil;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.SearchBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldFormula;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
@Service
public class IModuleFieldFormulaProviderImpl implements ModulePageService, IModuleFormulaService, IModuleFieldFormulaProvider {

    @Autowired
    private IModuleFieldDataService fieldDataService;

    @Autowired
    private IModuleProvider moduleProvider;

    @Autowired
    private IModuleFieldFormulaService fieldFormulaService;

    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(10, 20, 5L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(2048), new ThreadPoolExecutor.AbortPolicy());


    @Override
    public void updateFormulaFieldValue(Long moduleId) {
        Set<Long> moduleIds = moduleProvider.getUnionModuleIds(moduleId);
        List<ModuleEntity> modules = ModuleCacheUtil.getActiveByIds(moduleIds);
        updateFormulaFieldValue(modules);
    }

    @Override
    public void updateFormulaFieldValue(List<ModuleEntity> modules) {
        for (ModuleEntity module : modules) {
            try {
                UserUtil.setUser(module.getCreateUserId());
                updateFormulaFieldValueByModuleId(module.getModuleId());
            } finally {
                UserUtil.removeUser();
            }
        }
    }

    @Override
    public void updateFormulaFieldValueByModuleId(Long moduleId) {
        ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
        List<ModuleField> allFields = ModuleFieldCacheUtil.getByIdAndVersion(module.getModuleId(), module.getVersion());
        List<ModuleField> formulaFields = allFields.stream()
                .filter(f -> ObjectUtil.equal(ModuleFieldEnum.FORMULA.getType(), f.getType()))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(formulaFields)) {
            return;
        }
        List<ModuleFieldFormula> fieldFormulas = fieldFormulaService.getByModuleIdAndVersion(module.getModuleId(), module.getVersion());
        Map<Long, ModuleFieldFormula> fieldIdFormulaMap = fieldFormulas.stream().collect(Collectors.toMap(ModuleFieldFormula::getFieldId, Function.identity()));

        UserInfo user = UserCacheUtil.getUserInfo(module.getCreateUserId());
        THREAD_POOL.execute(() -> {
            try {
                UserUtil.setUser(user);
                AtomicInteger page = new AtomicInteger(1);
                dealData(module.getModuleId(), module.getVersion(), page, allFields, formulaFields, fieldIdFormulaMap);
            } finally {
                UserUtil.removeUser();
            }
        });
    }

    private void dealData(Long moduleId,
                          Integer version,
                          AtomicInteger page,
                          List<ModuleField> allFields,
                          List<ModuleField> formulaFields,
                          Map<Long, ModuleFieldFormula> fieldIdFormulaMap) {
        SearchBO searchBO = new SearchBO();
        searchBO.setPage(page.getAndIncrement());
        searchBO.setLimit(2000);
        searchBO.setModuleId(moduleId);
        searchBO.setAuthFilter(false);
        BasePage<Map<String, Object>> data = queryPageList(searchBO, moduleId);
        Map<String, ModuleField> fieldNameMap = allFields.stream().collect(Collectors.toMap(ModuleField::getFieldName, Function.identity()));
        for (Map<String, Object> map : data.getList()) {
            Long dataId = MapUtil.getLong(map, "dataId");
            Map<Long, Object> fieldIdValue = new HashMap<>(16);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();
                ModuleField field = fieldNameMap.get(fieldName);
                if (ObjectUtil.isNull(field)) {
                    continue;
                }
                fieldIdValue.put(field.getFieldId(), value);
            }
            for (ModuleField field : formulaFields) {
                ModuleFieldFormula fieldFormula = fieldIdFormulaMap.get(field.getFieldId());
                try {
                    String valueStr;
                    Object value = calculateFormula(moduleId, version, fieldIdValue, fieldFormula.getFormula());
                    if (Arrays.asList(1, 2, 3).contains(fieldFormula.getType())) {
                        if (value instanceof Number) {
                            if (value instanceof BigDecimal) {
                                BigDecimal bigDecimal = (BigDecimal) value;
                                bigDecimal = bigDecimal.setScale(Optional.ofNullable(field.getPrecisions()).orElse(0), BigDecimal.ROUND_HALF_UP);
                                valueStr = bigDecimal.toPlainString();
                            } else if (value instanceof Long) {
                                BigDecimal bigDecimal = BigDecimal.valueOf(((Long) value).longValue());
                                bigDecimal = bigDecimal.setScale(Optional.ofNullable(field.getPrecisions()).orElse(0), BigDecimal.ROUND_HALF_UP);
                                valueStr = bigDecimal.toPlainString();
                            } else if (value instanceof Double) {
                                BigDecimal bigDecimal = BigDecimal.valueOf(((Double) value).doubleValue());
                                bigDecimal = bigDecimal.setScale(Optional.ofNullable(field.getPrecisions()).orElse(0), BigDecimal.ROUND_HALF_UP);
                                valueStr = bigDecimal.toPlainString();
                            } else {
                                valueStr = value.toString();
                            }
                        } else {
                            valueStr = BigDecimal.ZERO.toString();
                        }
                    } else {
                        valueStr = value.toString();
                    }
                    // 更新数据库中字段的值
                    fieldDataService.saveOrUpdate(field, valueStr, dataId, version, moduleId);
                    // 更新ES
                    Map<String, Object> fieldValueMap = new HashMap<>();
                    fieldValueMap.put(field.getFieldName(), valueStr);
                    updateField(fieldValueMap, dataId, moduleId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (page.get() < data.getPages()) {
            dealData(moduleId, version, page, allFields, formulaFields, fieldIdFormulaMap);
        }
    }
}
