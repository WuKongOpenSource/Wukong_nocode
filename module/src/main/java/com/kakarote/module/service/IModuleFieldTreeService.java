package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleTreeBO;
import com.kakarote.module.entity.PO.ModuleFieldTree;

import java.util.List;

/**
 * @author : zjj
 * @desc : 树字段接口类
 * @since : 2022/12/1
 */
public interface IModuleFieldTreeService extends BaseService<ModuleFieldTree> {

    /**
     * 获取树字段配置
     *
     * @param moduleId 模块 ID
     * @param fieldId  字段 ID
     * @param version  版本号
     * @return data
     */
    List<ModuleTreeBO> queryTreeList(Long moduleId, Long fieldId, Integer version);


    /**
     * 根据模块id和版本号获取树字段
     *
     * @param moduleId 模块 ID
     * @param version  版本号
     * @return data
     */
    List<ModuleFieldTree> getByModuleIdAndVersion(Long moduleId, Integer version);
}
