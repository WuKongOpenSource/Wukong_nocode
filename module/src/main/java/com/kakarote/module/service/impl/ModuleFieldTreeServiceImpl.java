package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.BO.ModuleTreeBO;
import com.kakarote.module.entity.PO.ModuleFieldTree;
import com.kakarote.module.mapper.ModuleFieldTreeMapper;
import com.kakarote.module.service.IModuleFieldTreeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : zjj
 * @desc : 树字段实现类
 * @since : 2022/12/1
 */
@Service
public class ModuleFieldTreeServiceImpl extends BaseServiceImpl<ModuleFieldTreeMapper, ModuleFieldTree> implements IModuleFieldTreeService {

    @Override
    public List<ModuleTreeBO> queryTreeList(Long moduleId, Long fieldId, Integer version) {
        List<ModuleFieldTree> fieldTrees = lambdaQuery()
                .eq(ModuleFieldTree::getModuleId, moduleId)
                .eq(ModuleFieldTree::getFieldId, fieldId)
                .eq(ModuleFieldTree::getVersion, version)
                .orderByAsc(ModuleFieldTree::getSorting)
                .list();
        return fieldTrees.stream().map(options -> BeanUtil.copyProperties(options, ModuleTreeBO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ModuleFieldTree> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(ModuleFieldTree::getModuleId, moduleId)
                .eq(ModuleFieldTree::getVersion, version)
                .orderByAsc(ModuleFieldTree::getSorting)
                .list();
    }
}
