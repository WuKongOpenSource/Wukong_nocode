package com.kakarote.module.service;

import com.kakarote.module.entity.BO.ModuleFieldUnionSaveBO;

import java.util.List;

/**
 * @author : zjj
 * @since : 2022/12/28
 */
public interface IModuleFieldUnionProvider {

    /**
     * 查询数据关联(多选)字段的配置
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<ModuleFieldUnionSaveBO> query(Long moduleId, Integer version);
}
