package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.DoubleCheckResultBO;
import com.kakarote.module.entity.BO.ModuleFieldDataSaveBO;
import com.kakarote.module.entity.PO.FlowConditionData;
import com.kakarote.module.entity.PO.FlowUpdate;
import com.kakarote.module.entity.PO.ModuleEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-25 15:56
 */
public interface IFlowUpdateService extends BaseService<FlowUpdate> {

    FlowUpdate getByFlowId(Long flowId);

    List<FlowUpdate> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);

    /**
     * 插入操作字段值
     *
     * @param fieldDataSaveBO
     * @return 返回插入结果和重复的字段信息
     */
    DoubleCheckResultBO saveAndReturnDoubleCheckResult(ModuleFieldDataSaveBO fieldDataSaveBO);


    /**
     * 更新操作字段值
     *
     * @param updateCondition
     * @param dataIds
     * @param currentFieldData
     * @param module
     * @return 返回插更新结果和重复的字段信息
     */
    DoubleCheckResultBO updateAndReturnDoubleCheckResult(List<FlowConditionData> updateCondition,
                                                         Set<Long> dataIds,
                                                         Map<String, Object> currentFieldData,
                                                         ModuleEntity module);
}
