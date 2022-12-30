package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.entity.BO.ModuleTreeBO;
import com.kakarote.module.entity.BO.TreeDataBO;
import com.kakarote.module.entity.BO.TreeDataQueryBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.PO.ModuleTreeData;
import com.kakarote.module.service.IModuleFieldDataService;
import com.kakarote.module.service.IModuleFieldTreeService;
import com.kakarote.module.service.IModuleTreeDataProvider;
import com.kakarote.module.service.IModuleTreeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
@Service
public class ModuleTreeDataProviderImpl implements IModuleTreeDataProvider {

    @Autowired
    private IModuleTreeDataService treeDataService;

    @Autowired
    private IModuleFieldTreeService fieldTreeService;

    @Autowired
    private IModuleFieldDataService fieldDataService;

    @Override
    public TreeDataBO queryTreeDataByDataId(TreeDataQueryBO queryBO) {
        Long moduleId = queryBO.getModuleId();
        Long fieldId = queryBO.getFieldId();
        Long dataId = queryBO.getDataId();
        ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
        if (ObjectUtil.isNull(module)) {
            return null;
        }
        // 获取展示的字段 ID 列表
        List<ModuleTreeBO> treeBOS = fieldTreeService.queryTreeList(moduleId, fieldId, module.getVersion());
        List<Long> showFieldIds = treeBOS.stream().map(ModuleTreeBO::getShowField).collect(Collectors.toList());
        // 获取节点树的数据
        ModuleTreeData treeData = treeDataService.getByModuleIdFieldAndDataId(moduleId, fieldId, dataId);
        // 子集数据 ID
        String childId = treeData.getChildId();
        List<Long> childIds = JSON.parseArray(childId, Long.class);
        List<Long> parentIds = JSON.parseArray(treeData.getParentId(), Long.class);

        // 所有数据 ID
        List<Long> dataIds = new ArrayList<>();
        // 所有子级树的数据
        List<ModuleTreeData> childTreeDataList = new ArrayList<>();
        dataIds.add(dataId);
        if (CollUtil.isNotEmpty(childIds)) {
            dataIds.addAll(childIds);
            childTreeDataList = treeDataService.getByModuleIdFieldAndDataId(moduleId, fieldId, childIds);
        }
        List<ModuleFieldData> moduleFieldDataList = fieldDataService.queryFieldData(dataIds, showFieldIds);
        Map<Long, List<ModuleFieldData>> dataIdFieldDataListMap = moduleFieldDataList.stream().collect(Collectors.groupingBy(ModuleFieldData::getDataId));
        TreeDataBO result = new TreeDataBO();
        result.setDataId(dataId);
        result.setChildIds(childIds);
        result.setParentIds(parentIds);
        result.setFieldDataList(dataIdFieldDataListMap.get(dataId));
        if (CollUtil.isNotEmpty(childIds)) {
            // 当前数据子节点数据 ID 对应的子级 ID
            Map<Long, String> childDataIdChildIdMap = childTreeDataList.stream()
                    .filter(d -> StrUtil.isNotEmpty(d.getChildId()))
                    .collect(Collectors.toMap(ModuleTreeData::getDataId, ModuleTreeData::getChildId));
            for (Long id : childIds) {
                String childIdStr = childDataIdChildIdMap.get(id);
                TreeDataBO dataBO = new TreeDataBO();
                if (StrUtil.isNotEmpty(childIdStr)) {
                    dataBO.setChildIds(JSON.parseArray(childIdStr, Long.class));
                }
                dataBO.setDataId(id);
                dataBO.setFieldDataList(dataIdFieldDataListMap.get(id));
                result.addChild(dataBO);
            }
        }
        return result;
    }
}
