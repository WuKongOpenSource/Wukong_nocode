package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModulePrintTemplate;

/**
 * @author wwl
 * @date 2022/3/9 14:09
 */
public interface ModulePrintTemplateMapper extends BaseMapper<ModulePrintTemplate> {

    void removePrintRecord(Long templateId);


}
