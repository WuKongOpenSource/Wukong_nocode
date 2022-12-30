package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.BO.ModuleOptionsBO;
import com.kakarote.module.entity.PO.ModuleFieldOptions;
import com.kakarote.module.mapper.ModuleFieldOptionsMapper;
import com.kakarote.module.service.IModuleFieldOptionsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 字段选项表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-03-06
 */
@Service
public class ModuleFieldOptionsServiceImpl extends BaseServiceImpl<ModuleFieldOptionsMapper, ModuleFieldOptions> implements IModuleFieldOptionsService {

    /**
     * 查询字段选项信息
     *
     * @param fieldId 字段ID
     * @return data
     */
    @Override
    public List<ModuleOptionsBO> queryOptionsList(Long moduleId, Long fieldId, Integer version) {
        List<ModuleFieldOptions> fieldOptions = lambdaQuery()
                .eq(ModuleFieldOptions::getModuleId, moduleId)
                .eq(ModuleFieldOptions::getFieldId, fieldId)
                .eq(ModuleFieldOptions::getVersion, version)
                .orderByAsc(ModuleFieldOptions::getSorting).list();
        return fieldOptions.stream().map(options -> BeanUtil.copyProperties(options, ModuleOptionsBO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ModuleFieldOptions> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(ModuleFieldOptions::getModuleId, moduleId)
                .eq(ModuleFieldOptions::getVersion, version)
                .orderByAsc(ModuleFieldOptions::getSorting).list();
    }
}
