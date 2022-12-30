package com.kakarote.module.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.kakarote.module.constant.ModuleCacheKey;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.service.IFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: FlowCacheUtil
 * @description: 节点缓存
 * @date 2022/2/915:58
 */
@Component
public class FlowCacheUtil {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private IFlowService flowService;

    private static FlowCacheUtil ME;

    private static Cache<String, Flow> moduleFlowVersionCache;

    @PostConstruct
    public void init() {
        ME = this;
        QuickConfig moduleFlowVersionCacheConfig = QuickConfig.newBuilder(ModuleCacheKey.FLOW_VERSION_CACHE_NAME)
                .expire(Duration.ofSeconds(3600))
                .cacheType(CacheType.BOTH)
                .localLimit(50)
                .syncLocal(true)
                .build();
        moduleFlowVersionCache = cacheManager.getOrCreateCache(moduleFlowVersionCacheConfig);
    }

    public static Flow getByIdAndVersion(Long flowId, Integer version) {
        String key = flowId.toString() + "_" + version.toString();
        Flow flow = moduleFlowVersionCache.get(key);
        if (ObjectUtil.isNull(flow)) {
            flow = ME.flowService.getByFlowId(flowId, version);
            if (ObjectUtil.isNotNull(flow)) {
                moduleFlowVersionCache.put(key, flow);
            }
        }
        return flow;
    }

    public static List<Flow> getByIdAndVersion(Set<Long> flowIds, Integer version) {
        List<Flow> result = new ArrayList<>();
        for (Long flowId : flowIds) {
            Flow flow = getByIdAndVersion(flowId, version);
            if (ObjectUtil.isNotNull(flow)) {
                result.add(flow);
            }
        }
        return result;
    }

    public static void removeAll(Collection<Long> flowIds, Integer version) {
        Set<String> keys = new HashSet<>();
        for (Long flowId : flowIds) {
            String key = getKey(flowId, version);
            keys.add(key);
        }
        moduleFlowVersionCache.removeAll(keys);
    }

    public static void removeAll(List<Flow> flows) {
        Set<String> keys = flows.stream().map(i -> getKey(i.getFlowId(), i.getVersion())).collect(Collectors.toSet());
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        moduleFlowVersionCache.removeAll(keys);
    }

    public static void removeByModuleId(Long moduleId, Integer version) {
        List<Flow> flows = ME.flowService.getByModuleIdAndVersion(moduleId, version);
        removeAll(flows);
    }

    public static void remove(Long flowId, Integer version) {
        String key = getKey(flowId, version);
        moduleFlowVersionCache.remove(key);
    }

    public static void put(Flow flow) {
        String key = getKey(flow.getFlowId(), flow.getVersion());
        moduleFlowVersionCache.put(key, flow);
    }

    public static void putAll(List<Flow> flows) {
        Map<String, Flow> flowMap = flows.stream().collect(Collectors.toMap(i -> getKey(i.getFlowId(), i.getVersion()), Function.identity()));
        moduleFlowVersionCache.putAll(flowMap);
    }

    public static String getKey(Long id, Integer version) {
        return id.toString() + "_" + version.toString();
    }
}
