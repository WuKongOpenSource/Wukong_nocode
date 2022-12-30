package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.ModuleFieldConfig;
import com.kakarote.module.mapper.ModuleFieldConfigMapper;
import com.kakarote.module.service.IModuleFieldConfigService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 字段配置表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@Service
public class ModuleFieldConfigServiceImpl extends BaseServiceImpl<ModuleFieldConfigMapper, ModuleFieldConfig> implements IModuleFieldConfigService {


    @Override
    public ModuleFieldConfig getByFieldNameAndType(String fieldName, Integer type) {
        return lambdaQuery()
                .eq(ModuleFieldConfig::getFieldName, fieldName)
                .eq(ModuleFieldConfig::getFieldType, type)
                .one();
    }
}
