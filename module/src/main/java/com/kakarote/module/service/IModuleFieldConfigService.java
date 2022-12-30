package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModuleFieldConfig;

/**
 * <p>
 * 字段配置表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
public interface IModuleFieldConfigService extends BaseService<ModuleFieldConfig> {


    /**
     * 根据字段名和字段类型获取字段配置信息
     *
     * @param fieldName 字段名
     * @param type      字段类型
     * @return 字段配置信息
     */
    ModuleFieldConfig getByFieldNameAndType(String fieldName, Integer type);

}
