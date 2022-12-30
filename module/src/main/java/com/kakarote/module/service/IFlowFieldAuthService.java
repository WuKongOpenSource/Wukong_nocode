package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.FlowFieldAuth;

import java.util.List;

public interface IFlowFieldAuthService extends BaseService<FlowFieldAuth> {

    /**
     * 获取指定节的字段授权
     *
     * @param moduleId 模块 ID
     * @param version  版本号
     * @param flowId   节点 ID
     * @return
     */
    FlowFieldAuth getByModuleIdAndFlowId(Long moduleId, Integer version, Long flowId);


    /**
     * 获取指定流程的所有字段授权配置
     *
     * @param moduleId       模块 ID
     * @param version        版本号
     * @param flowMetaDataId 流程元数据 ID
     * @return
     */
    List<FlowFieldAuth> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);

}
