package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.entity.BO.ModuleTreeBO;
import com.kakarote.module.entity.BO.TreeDataBO;
import com.kakarote.module.entity.BO.TreeDataQueryBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.PO.ModuleTreeData;
import com.kakarote.module.mapper.ModuleTreeDataMapper;
import com.kakarote.module.service.IModuleFieldDataService;
import com.kakarote.module.service.IModuleFieldTreeService;
import com.kakarote.module.service.IModuleTreeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : zjj
 * @desc : 树字段数据服务实现类
 * @since : 2022/12/2
 */
@Service
public class ModuleTreeDataServiceImpl extends BaseServiceImpl<ModuleTreeDataMapper, ModuleTreeData> implements IModuleTreeDataService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Long moduleId, Long fieldId, Long dataId, String childId) {
        List<Long> childIds = JSON.parseArray(childId, Long.class);
        ModuleTreeData treeData = this.getByModuleIdFieldAndDataId(moduleId, fieldId, dataId);
        // region 保存当前数据的树型结构数据
        // 修改之前当前数据的子集数据 ID
        String oldChildId = null;
        if (StrUtil.isEmpty(childId)) {
            if (ObjectUtil.isNotNull(treeData)) {
                oldChildId = treeData.getChildId();
                removeById(treeData.getId());
            }
        } else {
            // 如果当前数据已存在树型结构
            if (ObjectUtil.isNotNull(treeData)) {
                oldChildId = treeData.getChildId();
                treeData.setChildId(JSON.toJSONString(childIds));
            } else {
                // 当前数据不存在树型结构
                treeData = new ModuleTreeData();
                treeData.setModuleId(moduleId);
                treeData.setFieldId(fieldId);
                treeData.setDataId(dataId);
                treeData.setChildId(JSON.toJSONString(childIds));
                treeData.setCreateTime(DateTime.now());
            }
            saveOrUpdate(treeData);
        }
        // endregion

        List<Long> oldChildIds = JSON.parseArray(oldChildId, Long.class);
        // 待删除的子树父级 ID 的数据
        List<Long> toRemove = Optional.ofNullable(oldChildIds).orElse(Collections.emptyList())
                .stream().filter(i -> !childIds.contains(i)).collect(Collectors.toList());
        // 待添加的子树父级 ID 的数据
        List<Long> toAdd = Optional.ofNullable(childIds).orElse(Collections.emptyList())
                .stream().filter(i -> !CollUtil.contains(oldChildIds, i)).collect(Collectors.toList());

        List<Long> toUpdate = new ArrayList<>();
        toUpdate.addAll(toAdd);
        toUpdate.addAll(toRemove);
        if (CollUtil.isEmpty(toUpdate)) {
            return;
        }
        // 获取当前数据的所有待修改子集数据的树型结构数据
        List<ModuleTreeData> childTreeDataList = this.getByModuleIdFieldAndDataId(moduleId, fieldId, toUpdate);
        Map<Long, ModuleTreeData> dataIdEntityMap = childTreeDataList.stream()
                .collect(Collectors.toMap(ModuleTreeData::getDataId, Function.identity()));

        // 待修改的树数据
        List<ModuleTreeData> childTreeDataToUpdate = new ArrayList<>();
        // 待删除的树数据
        List<ModuleTreeData> childTreeDataToRemove = new ArrayList<>();
        // 需要添加子树父级 ID 的数据
        for (Long id : toAdd) {
            ModuleTreeData childTreeData = dataIdEntityMap.get(id);
            if (ObjectUtil.isNull(childTreeData)) {
                childTreeData = new ModuleTreeData();
                childTreeData.setModuleId(moduleId);
                childTreeData.setFieldId(fieldId);
                childTreeData.setDataId(id);
                childTreeData.setParentId(JSON.toJSONString(Collections.singletonList(dataId)));
                childTreeData.setCreateTime(DateTime.now());
            } else {
                String parentId = childTreeData.getParentId();
                List<Long> parentIds = JSON.parseArray(parentId, Long.class);
                if (CollUtil.isEmpty(parentIds)) {
                    parentIds = new ArrayList<>();
                }
                parentIds.add(dataId);
                childTreeData.setParentId(JSON.toJSONString(parentIds));
            }
            childTreeDataToUpdate.add(childTreeData);
        }
        // 需要删除子树父级 ID 的数据
        for (Long id : toRemove) {
            ModuleTreeData childTreeData = dataIdEntityMap.get(id);
            if (ObjectUtil.isNotNull(childTreeData)) {
                // 父级 ID
                String parentId = childTreeData.getParentId();
                List<Long> parentIds = JSON.parseArray(parentId, Long.class);
                CollUtil.removeAny(parentIds, dataId);
                // 子集 ID
                String dataChildId = childTreeData.getChildId();
                List<Long> dataChildIds = JSON.parseArray(dataChildId, Long.class);
                // 如果删除父级 ID 后，子树的父级 ID 为空且子集 ID 也为空，则直接删除子树的数据
                if (CollUtil.isEmpty(parentIds) && CollUtil.isEmpty(dataChildIds)) {
                    childTreeDataToRemove.add(childTreeData);
                } else {
                    childTreeData.setParentId(JSON.toJSONString(parentIds));
                    childTreeDataToUpdate.add(childTreeData);
                }
            }
        }
        saveOrUpdateBatch(childTreeDataToUpdate);
        if (CollUtil.isNotEmpty(childTreeDataToRemove)) {
            List<Long> toRemoveIdList = childTreeDataToRemove.stream().map(ModuleTreeData::getId).collect(Collectors.toList());
            removeByIds(toRemoveIdList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long moduleId, List<Long> dataIds) {
        List<ModuleTreeData> moduleTreeDataList = lambdaQuery()
                .eq(ModuleTreeData::getModuleId, moduleId)
                .in(ModuleTreeData::getDataId, dataIds)
                .list();
        // 删除当前树
        lambdaUpdate()
                .eq(ModuleTreeData::getModuleId, moduleId)
                .in(ModuleTreeData::getDataId, dataIds)
                .remove();

        // 待修改的树数据
        List<ModuleTreeData> childTreeDataToUpdate = new ArrayList<>();
        // 待删除的树数据
        List<ModuleTreeData> childTreeDataToRemove = new ArrayList<>();
        // 查询当前树的父节点
        Set<Long> parentIds = new HashSet<>();
        moduleTreeDataList.stream()
                .filter(p -> StrUtil.isNotEmpty(p.getParentId()))
                .forEach(p -> {
                    List<Long> ids = JSON.parseArray(p.getParentId(), Long.class);
                    if (CollUtil.isNotEmpty(ids)) {
                        parentIds.addAll(ids);
                    }
                });
        if (CollUtil.isNotEmpty(parentIds)) {
            List<ModuleTreeData> parentTreeDataList = lambdaQuery()
                    .eq(ModuleTreeData::getModuleId, moduleId)
                    .in(ModuleTreeData::getDataId, parentIds)
                    .list();
            for (ModuleTreeData treeData : parentTreeDataList) {
                // 父级 ID
                String treeDataParentId = treeData.getParentId();
                List<Long> treeDataParentIds = JSON.parseArray(treeDataParentId, Long.class);
                // 子集 ID
                String dataChildId = treeData.getChildId();
                if (StrUtil.isNotEmpty(dataChildId)) {
                    List<Long> dataChildIds = JSON.parseArray(dataChildId, Long.class);
                    dataChildIds.removeAll(dataIds);
                    // 如果删除树的子集 ID后，树子的父级 ID 为空且子集 ID 也为空，则直接删除子树的数据
                    if (CollUtil.isEmpty(treeDataParentIds) && CollUtil.isEmpty(dataChildIds)) {
                        childTreeDataToRemove.add(treeData);
                    } else {
                        treeData.setChildId(JSON.toJSONString(dataChildIds));
                        childTreeDataToUpdate.add(treeData);
                    }
                }
            }
        }

        // 查询当前树的子节点
        Set<Long> childIds = new HashSet<>();
        moduleTreeDataList.stream()
                .filter(p -> StrUtil.isNotEmpty(p.getChildId()))
                .forEach(p -> {
                    List<Long> ids = JSON.parseArray(p.getChildId(), Long.class);
                    childIds.addAll(ids);
                });
        if (CollUtil.isNotEmpty(childIds)) {
            List<ModuleTreeData> childTreeDataList = lambdaQuery()
                    .eq(ModuleTreeData::getModuleId, moduleId)
                    .in(ModuleTreeData::getDataId, childIds)
                    .list();
            for (ModuleTreeData treeData : childTreeDataList) {
                // 父级 ID
                String treeDataParentId = treeData.getParentId();
                // 子集 ID
                String dataChildId = treeData.getChildId();
                List<Long> dataChildIds = JSON.parseArray(dataChildId, Long.class);

                if (StrUtil.isNotEmpty(treeDataParentId)) {
                    List<Long> treeDataParentIds = JSON.parseArray(treeDataParentId, Long.class);
                    treeDataParentIds.removeAll(dataIds);
                    // 如果删除树的子集 ID后，树子的父级 ID 为空且子集 ID 也为空，则直接删除子树的数据
                    if (CollUtil.isEmpty(treeDataParentIds) && CollUtil.isEmpty(dataChildIds)) {
                        childTreeDataToRemove.add(treeData);
                    } else {
                        treeData.setParentId(JSON.toJSONString(treeDataParentIds));
                        childTreeDataToUpdate.add(treeData);
                    }
                }
            }
        }

        if (CollUtil.isNotEmpty(childTreeDataToUpdate)) {
            saveOrUpdateBatch(childTreeDataToUpdate);
        }
        if (CollUtil.isNotEmpty(childTreeDataToRemove)) {
            List<Long> toRemoveIdList = childTreeDataToRemove.stream().map(ModuleTreeData::getId).collect(Collectors.toList());
            removeByIds(toRemoveIdList);
        }
    }

    @Override
    public ModuleTreeData getByModuleIdFieldAndDataId(Long moduleId, Long fieldId, Long dataId) {
        return lambdaQuery()
                .eq(ModuleTreeData::getModuleId, moduleId)
                .eq(ModuleTreeData::getFieldId, fieldId)
                .eq(ModuleTreeData::getDataId, dataId)
                .one();
    }

    @Override
    public List<ModuleTreeData> getByModuleIdFieldAndDataId(Long moduleId, Long fieldId, List<Long> dataIds) {
        return lambdaQuery()
                .eq(ModuleTreeData::getModuleId, moduleId)
                .eq(ModuleTreeData::getFieldId, fieldId)
                .in(ModuleTreeData::getDataId, dataIds)
                .list();
    }
}
