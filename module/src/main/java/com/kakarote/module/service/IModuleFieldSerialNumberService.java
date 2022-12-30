package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldSerialNumber;

import java.util.List;
import java.util.Map;

/**
 * 自定义编号字段
 * @author wwl
 * @date 20220304
 */
public interface IModuleFieldSerialNumberService  extends BaseService<ModuleFieldSerialNumber> {
    /**
     * 生成唯一编号字段
     *
     * @param map 表单字段
     * @param fields
     * @param fieldId
     * @param moduleId
     * @param version
     * @return
     */
    String generateNumber(Map<String, Object> map, List<ModuleField> fields, Long fieldId, Long moduleId, int version);
}
