package com.kakarote.module.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.RefreshPolicy;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.kakarote.module.constant.ModuleCacheKey;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.service.IModuleFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: ModuleFieldCacheUtil
 * @description: 字段缓存
 * @date 2022/2/915:30
 */
@Component
public class ModuleFieldCacheUtil {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IModuleFieldService fieldService;

    private static ModuleFieldCacheUtil ME;

    // 字段缓存
    private static Cache<String, ModuleField> fieldVersionCache;

    // 模块-字段缓存
    private static Cache<String, List<ModuleField>> moduleFieldVersionCache;

    @PostConstruct
    public void init() {
        ME = this;
        QuickConfig fieldVersionCacheConfig = QuickConfig.newBuilder(ModuleCacheKey.FIELD_VERSION_CACHE_NAME)
                .expire(Duration.ofSeconds(3600))
                .cacheType(CacheType.BOTH)
                .localLimit(50)
                .syncLocal(true)
                .build();
        fieldVersionCache = cacheManager.getOrCreateCache(fieldVersionCacheConfig);

        QuickConfig moduleFieldVersionCacheConfig = QuickConfig.newBuilder(ModuleCacheKey.FIELD_VERSION_CACHE_NAME)
                .expire(Duration.ofSeconds(3600))
                .refreshPolicy(RefreshPolicy.newPolicy(60, TimeUnit.SECONDS).stopRefreshAfterLastAccess(100, TimeUnit.SECONDS))
                .penetrationProtect(true)
                .build();
        moduleFieldVersionCache = cacheManager.getOrCreateCache(moduleFieldVersionCacheConfig);
    }

    public static ModuleField getByIdAndVersion(Long moduleId, Long fieldId, Integer version) {
        String key = getKey(moduleId, fieldId, version);
        ModuleField field = fieldVersionCache.get(key);
        if (ObjectUtil.isNull(field)) {
            field = ME.fieldService.getByFieldId(moduleId, fieldId, version);
            if (ObjectUtil.isNotNull(field)) {
                fieldVersionCache.put(key, field);
            }
        }
        return field;
    }

    public static List<ModuleField> getByIdAndVersion(Long moduleId, Set<Long> fieldIds, Integer version) {
        List<ModuleField> result = new ArrayList<>();
        for (Long fieldId : fieldIds) {
            ModuleField field = getByIdAndVersion(moduleId, fieldId, version);
            result.add(field);
        }
        return result;
    }

    public static List<ModuleField> getByIdAndVersion(Long moduleId, Integer version) {
        String key = getKey(moduleId, version);
        List<ModuleField> fields = moduleFieldVersionCache.get(key);
        if (CollUtil.isEmpty(fields)) {
            fields = ME.fieldService.getByModuleIdAndVersion(moduleId, version, null);
            if (CollUtil.isNotEmpty(fields)) {
                moduleFieldVersionCache.put(key, fields);
            }
        }
        return fields;
    }

    public static void removeAll(Long moduleId, Collection<Long> fieldIds, Integer version) {
        Set<String> keys = new HashSet<>();
        for (Long fieldId : fieldIds) {
            String key = getKey(moduleId, fieldId, version);
            keys.add(key);
        }
        fieldVersionCache.removeAll(keys);
    }

    public static void remove(Long moduleId, Long fieldId, Integer version) {
        String key = getKey(moduleId, fieldId, version);
        fieldVersionCache.remove(key);
    }

    public static void remove(Long moduleId, Integer version) {
        String key = getKey(moduleId, version);
        moduleFieldVersionCache.remove(key);
    }

    public static void removeByModuleId(Long moduleId, Integer version) {
        List<ModuleField> fields = ME.fieldService.getByModuleIdAndVersion(moduleId, version, null);
        Set<String> keys = fields.stream().map(i -> getKey(i.getModuleId(), i.getFieldId(), i.getVersion())).collect(Collectors.toSet());
        // wwl 20220411 增加数组是否为空判断，如果为空直接执行删除，会报“ERR wrong number of arguments for 'del' command”
        if (CollUtil.isNotEmpty(keys)) {
            fieldVersionCache.removeAll(keys);
        }
    }

    public static void put(ModuleField field) {
        String key = getKey(field.getModuleId(), field.getFieldId(), field.getVersion());
        fieldVersionCache.put(key, field);
    }

    public static void put(Long moduleId, Integer version, List<ModuleField> fields) {
        moduleFieldVersionCache.put(getKey(moduleId, version), fields);
    }

    public static void putAll(List<ModuleField> fields) {
        Map<String, ModuleField> fieldMap = fields.stream().collect(Collectors.toMap(i -> getKey(i.getModuleId(), i.getFieldId(), i.getVersion()), Function.identity()));
        fieldVersionCache.putAll(fieldMap);
    }

    public static String getKey(Long moduleId, Long id, Integer version) {
        return StrUtil.join("_", moduleId, id, version);
    }

    public static String getKey(Long moduleId, Integer version) {
        return StrUtil.join("_", moduleId, version);
    }
}
