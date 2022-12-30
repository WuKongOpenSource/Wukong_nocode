package com.kakarote.module.common;

import cn.hutool.core.util.ObjectUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.kakarote.module.constant.ModuleCacheKey;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.service.IModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zjj
 * @title: ModuleCacheUtil
 * @description: 模块缓存工具
 * @date 2022/2/910:59
 */
@Component
public class ModuleCacheUtil {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private IModuleService moduleService;

    private static ModuleCacheUtil ME;

    private static Cache<String, ModuleEntity> moduleVersionCache;

    private static Cache<Long, ModuleEntity> activeModuleCache;

    @PostConstruct
    public void init() {
        ME = this;
        QuickConfig moduleVersionCacheConfig = QuickConfig.newBuilder(ModuleCacheKey.MODULE_VERSION_CACHE_NAME)
                .expire(Duration.ofSeconds(3600))
                .cacheType(CacheType.BOTH)
                .localLimit(50)
                .syncLocal(true)
                .build();
        moduleVersionCache = cacheManager.getOrCreateCache(moduleVersionCacheConfig);

        QuickConfig activeModuleCacheConfig = QuickConfig.newBuilder(ModuleCacheKey.ACTIVE_MODULE_CACHE_NAME)
                .expire(Duration.ofSeconds(3600))
                .cacheType(CacheType.BOTH)
                .localLimit(50)
                .syncLocal(true)
                .build();
        activeModuleCache = cacheManager.getOrCreateCache(activeModuleCacheConfig);
    }


    public static ModuleEntity getByIdAndVersion(Long moduleId, Integer version) {
        String key = moduleId.toString() + "_" + version.toString();
        ModuleEntity module = moduleVersionCache.get(key);
        if (ObjectUtil.isNull(module)) {
            module = ME.moduleService.getByModuleIdAndVersion(moduleId, version);
            if (ObjectUtil.isNotNull(module)) {
                moduleVersionCache.put(key, module);
            }
        }
        return module;
    }

    public static void remove(Long moduleId, Integer version) {
        String key = getKey(moduleId, version);
        moduleVersionCache.remove(key);
    }

    /**
     * 删除所有版本
     *
     * @param moduleId
     * @param maxVersion
     */
    public static void removeAllVersion(Long moduleId, Integer maxVersion) {
        for (Integer i = 0; i <= maxVersion; i++) {
            String key = getKey(moduleId, i);
            moduleVersionCache.remove(key);
        }
    }

    public static ModuleEntity getActiveById(Long moduleId) {
        ModuleEntity module = activeModuleCache.get(moduleId);
        if (ObjectUtil.isNull(module)) {
            module = ME.moduleService.getNormal(moduleId);
            if (ObjectUtil.isNotNull(module)) {
                activeModuleCache.put(moduleId, module);
            }
        }
        return module;
    }

    public static List<ModuleEntity> getActiveByIds(Collection<Long> moduleIds) {
        List<ModuleEntity> modules = new ArrayList<>();
        for (Long moduleId : moduleIds) {
            ModuleEntity module = getActiveById(moduleId);
            if (ObjectUtil.isNotNull(module)) {
                modules.add(module);
            }
        }
        return modules;
    }

    public static void setActive(Long moduleId) {
        activeModuleCache.remove(moduleId);
        ModuleEntity module = ME.moduleService.getNormal(moduleId);
        activeModuleCache.put(moduleId, module);
    }

    public static String getKey(Long id, Integer version) {
        return id.toString() + "_" + version.toString();
    }
}
