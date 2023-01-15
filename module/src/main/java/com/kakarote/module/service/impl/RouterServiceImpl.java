package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.BaseUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.entity.BO.RouterBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleGroup;
import com.kakarote.module.entity.PO.ModuleGroupSort;
import com.kakarote.module.entity.PO.Router;
import com.kakarote.module.mapper.RouterMapper;
import com.kakarote.module.service.IRouterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 全局路由配置表 服务实现类
 *
 * @author zjj
 * @since 2022-08-08
 */
@Service
public class RouterServiceImpl extends BaseServiceImpl<RouterMapper, Router> implements IRouterService {

    @Override
    public void groupTransferRouter(List<ModuleGroup> groups,
                                    List<ModuleGroupSort> groupSorts,
                                    Map<Long, Long> oldNewModuleIdMap,
                                    List<ModuleEntity> modules,
                                    Long applicationId,
                                    List<Router> routers) {
        Map<Long, ModuleGroup> groupIdEntityMap = Optional.ofNullable(groups)
                .map(g -> g.stream().collect(Collectors.toMap(ModuleGroup::getId, Function.identity())))
                .orElse(new HashMap<>());
        Map<Long, ModuleEntity> moduleIdEntityMap = modules.stream().collect(Collectors.toMap(ModuleEntity::getModuleId, Function.identity()));
        if (CollUtil.isNotEmpty(groupSorts)) {
            List<ModuleGroupSort> parents = groupSorts.stream()
                    .filter(g -> ObjectUtil.isNull(g.getGroupId()) || ObjectUtil.isNull(g.getModuleId()))
                    .sorted(Comparator.comparing(ModuleGroupSort::getSort))
                    .collect(Collectors.toList());
            AtomicInteger atomicInteger = new AtomicInteger(0);
            for (ModuleGroupSort groupSort : parents) {
                Long routerId = BaseUtil.getNextId();
                Router router = this.transferRouter(groupIdEntityMap, moduleIdEntityMap, oldNewModuleIdMap, groupSort, routerId, applicationId);
                if (ObjectUtil.isNull(router)) {
                    continue;
                }
                router.setSort(atomicInteger.getAndIncrement());
                routers.add(router);
                // 分组
                if (ObjectUtil.isNotNull(groupSort.getGroupId())) {
                    // 分组下的模块
                    List<ModuleGroupSort> groupModules = groupSorts.stream()
                            .filter(g -> ObjectUtil.equal(groupSort.getGroupId(), g.getGroupId())
                                    && ObjectUtil.isNotNull(g.getModuleId()))
                            .sorted(Comparator.comparing(ModuleGroupSort::getSort))
                            .collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(groupModules)) {
                        for (ModuleGroupSort groupModule : groupModules) {
                            Router subRouter = this.transferRouter(groupIdEntityMap, moduleIdEntityMap, oldNewModuleIdMap, groupModule, routerId, applicationId);
                            if (ObjectUtil.isNotNull(subRouter)) {
                                subRouter.setSort(atomicInteger.getAndIncrement());
                                subRouter.setParentId(routerId);
                                routers.add(subRouter);
                            }
                        }
                    }
                }
            }
        }
    }

    private Router transferRouter(Map<Long, ModuleGroup> groupIdEntityMap,
                                  Map<Long, ModuleEntity> moduleIdEntityMap,
                                  Map<Long, Long> oldNewModuleIdMap,
                                  ModuleGroupSort groupSort,
                                  Long parentId,
                                  Long applicationId) {
        Router router = new Router();
        router.setSourceApplicationId(applicationId);
        router.setApplicationId(applicationId);
        // 分组
        if (ObjectUtil.isNull(groupSort.getModuleId())) {
            ModuleGroup moduleGroup = groupIdEntityMap.get(groupSort.getGroupId());
            if (ObjectUtil.isNull(moduleGroup)) {
                return null;
            }
            router.setRouterId(parentId);
            router.setParentId(0L);
            router.setIcon(moduleGroup.getIcon());
            router.setTitle(moduleGroup.getGroupName());
            router.setPath(applicationId.toString());
            router.setType(1);
        } else if (ObjectUtil.isNull(groupSort.getGroupId())) { // 模块
            Long newModuleId = oldNewModuleIdMap.get(groupSort.getModuleId());
            ModuleEntity module = moduleIdEntityMap.get(newModuleId);
            if (ObjectUtil.isNull(module)) {
                return null;
            }
            router.setRouterId(parentId);
            router.setParentId(0L);
            router.setIcon(module.getIcon());
            router.setTitle(module.getName());
            router.setPath(newModuleId.toString());
            router.setType(0);
            router.setSourceModuleId(newModuleId);
        } else { // 分组下的模块
            Long newModuleId = oldNewModuleIdMap.get(groupSort.getModuleId());
            ModuleEntity module = moduleIdEntityMap.get(newModuleId);
            if (ObjectUtil.isNull(module)) {
                return null;
            }
            router.setRouterId(BaseUtil.getNextId());
            router.setIcon(module.getIcon());
            router.setTitle(module.getName());
            router.setPath(String.format("%s/%s/%s/%s", applicationId, parentId, "subs", newModuleId));
            router.setType(0);
            router.setSourceModuleId(newModuleId);
        }
        // 暂时没有 BI，全部默认为无代码
        router.setComponent("@/views/userApp/index");
        router.setCreateTime(LocalDateTime.now());
        router.setCreateUserId(UserUtil.getUserId());
        return router;
    }

    @Override
    public void transferRouter(List<Router> routers, Map<Long, Long> oldNewModuleIdMap, Long applicationId) {
        if (CollUtil.isNotEmpty(routers)) {
            for (Router router : routers) {
                router.setId(null);
                // 分组
                String path = router.getPath();
                Long moduleId = router.getSourceModuleId();
                Long oldApplicationId = router.getApplicationId();
                Long newModuleId = oldNewModuleIdMap.get(moduleId);
                if (ObjectUtil.isNotNull(newModuleId)) {
                    router.setSourceModuleId(newModuleId);
                }
                path = path.replace(String.valueOf(oldApplicationId), String.valueOf(applicationId))
                        .replace(String.valueOf(moduleId), String.valueOf(newModuleId));
                router.setPath(path);
                router.setApplicationId(applicationId);
                router.setSourceModuleId(applicationId);
                router.setCreateTime(LocalDateTime.now());
                router.setCreateUserId(UserUtil.getUserId());
            }
        }
    }

    @Override
    public List<RouterBO> listRouter(Long applicationId) {
        List<Router> routers = lambdaQuery()
                .eq(Router::getApplicationId, applicationId)
                .orderByAsc(Router::getSort)
                .list();
        return routers.stream()
                .map(r -> BeanUtil.copyProperties(r, RouterBO.class))
                .collect(Collectors.toList());
    }
}
