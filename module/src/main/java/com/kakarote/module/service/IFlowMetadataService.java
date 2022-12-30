package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.FlowMetadata;

import java.util.List;

/**
 * <p>
 * 模块自定义流程元数据表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowMetadataService extends BaseService<FlowMetadata> {

    /**
     *  查询流程元数据
     * 
     * @param moduleId
     * @param version
     * @param typeId
     * @param type
     * @return
     */
    FlowMetadata getByModuleId(Long moduleId, Integer version, Long typeId, Integer type);

    /**
     *  获取指定版本的流程元数据
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<FlowMetadata> getByModuleIdAndVersion(Long moduleId, Integer version);

    /**
     *  获取流程元数据
     *
     * @param metaDataId
     * @return
     */
    FlowMetadata getByMetadataId(Long metaDataId);

    /**
     *  获取模块流程管理员
     *
     * @param metaDataId
     * @return
     */
    List<Long> getManagerUserIds(Long metaDataId);
}
