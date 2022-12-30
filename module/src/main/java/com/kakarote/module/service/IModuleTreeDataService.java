package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.TreeDataBO;
import com.kakarote.module.entity.BO.TreeDataQueryBO;
import com.kakarote.module.entity.PO.ModuleTreeData;

import java.util.List;

/**
 * @author : zjj
 * @desc : 树字段数据服务接口类
 * @since : 2022/12/2
 */
public interface IModuleTreeDataService extends BaseService<ModuleTreeData> {

    /**
     * 保存数字段数据
     *
     * @param moduleId 模块 ID
     * @param fieldId  字段 ID
     * @param dataId   数据 ID
     * @param childId  子集数据 ID
     */
    void save(Long moduleId, Long fieldId, Long dataId, String childId);

    /**
     * 删除数据
     *
     * @param moduleId 模块 ID
     * @param dataIds  数据 ID 列表
     */
    void delete(Long moduleId, List<Long> dataIds);

    /**
     * 获取指定树字段的指定节点数据
     *
     * @param moduleId 模块 ID
     * @param fieldId  字段 ID
     * @param dataId   数据 ID
     * @return data
     */
    ModuleTreeData getByModuleIdFieldAndDataId(Long moduleId, Long fieldId, Long dataId);

    /**
     * 获取指定树字段的指定节点数据列表
     *
     * @param moduleId 模块 ID
     * @param fieldId  字段 ID
     * @param dataIds  数据 ID 列表
     * @return data
     */
    List<ModuleTreeData> getByModuleIdFieldAndDataId(Long moduleId, Long fieldId, List<Long> dataIds);
}
