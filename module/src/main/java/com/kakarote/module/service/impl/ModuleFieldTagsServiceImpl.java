package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.BO.ModuleTagsBO;
import com.kakarote.module.entity.PO.ModuleFieldTags;
import com.kakarote.module.mapper.ModuleFieldTagsMapper;
import com.kakarote.module.service.IModuleFieldTagsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: ModuleFieldTagsServiceImpl
 * @description: 字段标签选项 服务实现类
 * @date 2022/3/314:51
 */
@Service
public class ModuleFieldTagsServiceImpl extends BaseServiceImpl<ModuleFieldTagsMapper, ModuleFieldTags> implements IModuleFieldTagsService {

    @Override
    public List<ModuleTagsBO> queryTagList(Long moduleId, Long fieldId, Integer version) {
        List<ModuleFieldTags> fieldTags = lambdaQuery()
                .eq(ModuleFieldTags::getModuleId, moduleId)
                .eq(ModuleFieldTags::getFieldId, fieldId)
                .eq(ModuleFieldTags::getVersion, version)
                .orderByAsc(ModuleFieldTags::getSorting).list();
        return fieldTags.stream().map(options -> BeanUtil.copyProperties(options, ModuleTagsBO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ModuleFieldTags> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(ModuleFieldTags::getModuleId, moduleId)
                .eq(ModuleFieldTags::getVersion, version)
                .orderByAsc(ModuleFieldTags::getSorting).list();
    }
}
