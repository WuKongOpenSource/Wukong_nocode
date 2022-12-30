package com.kakarote.module.service;

import com.kakarote.module.entity.BO.ModuleFieldDataResponseBO;
import com.kakarote.module.entity.BO.ModuleFieldDataSaveBO;
import com.kakarote.module.entity.BO.ModuleFieldValueBO;
import com.kakarote.module.entity.PO.FlowConditionData;
import com.kakarote.module.entity.PO.ModuleEntity;

import java.util.List;
import java.util.Map;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
public interface IModuleFieldDataProvider {

    /**
     * 保存模块字段值
     *
     * @param fieldDataBO
     * @return
     */
    Long save(ModuleFieldDataSaveBO fieldDataBO);

    /**
     * 查询指定数据的指定字段的值
     *
     * @param moduleId   模块ID
     * @param version    版本号
     * @param dataId     数据ID
     * @param fieldNames 字段名称
     * @return
     */
    List<ModuleFieldValueBO> queryValueMap(Long moduleId, Integer version, Long dataId, List<String> fieldNames);

    /**
     * 获取字段值
     *
     * @param dataId
     * @param fieldId
     * @return
     */
    String queryValue(Long dataId, Long fieldId);

    /**
     * 获取字段值map
     *
     * @param dataId 数据ID
     * @return
     */
    Map<String, Object> queryFieldNameDataMap(Long dataId);

    /**
     * 获取字段值对象
     *
     * @param dataId
     * @param replaceMask 是否替换掩码
     * @return
     */
    ModuleFieldDataResponseBO queryById(Long dataId, Boolean replaceMask);

    /**
     * 构建字段值保存对象
     *
     * @param targetModuleId        目标模块ID
     * @param flowConditionDataList 目标模块添加/更新字段值规则
     * @param currentModule         当前模块
     * @param dataMap               当前模块字段值
     * @return
     */
    ModuleFieldDataSaveBO buildFieldSaveBO(Long targetModuleId, List<FlowConditionData> flowConditionDataList,
                                           ModuleEntity currentModule, Map<String, Object> dataMap);
}
