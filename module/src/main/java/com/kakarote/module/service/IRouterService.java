package com.kakarote.module.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.RouterBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleGroup;
import com.kakarote.module.entity.PO.ModuleGroupSort;
import com.kakarote.module.entity.PO.Router;

import java.util.List;
import java.util.Map;

/**
 * 全局路由配置表 服务类
 *
 * @author zjj
 * @since 2022-08-08
 */
public interface IRouterService extends BaseService<Router> {

    /**
     * 分组转换路由
     *
     * @param groups            分组信息
     * @param groupSorts        分组排序信息
     * @param oldNewModuleIdMap 新旧模块ID对应关系
     * @param modules           模块
     * @param applicationId     应用 ID
     */
    void groupTransferRouter(List<ModuleGroup> groups,
                             List<ModuleGroupSort> groupSorts,
                             Map<Long, Long> oldNewModuleIdMap,
                             List<ModuleEntity> modules,
                             Long applicationId,
                             List<Router> routers);

    /**
     * 路由转换
     *
     * @param routers           路由信息
     * @param oldNewModuleIdMap 新旧模块ID对应关系
     * @param applicationId     应用 ID
     */
    void transferRouter(List<Router> routers,
                        Map<Long, Long> oldNewModuleIdMap,
                        Long applicationId);

    /**
     * 查询应用的路由信息
     *
     * @param applicationId 应用 ID
     * @return
     */
    List<RouterBO> listRouter(Long applicationId);
}
