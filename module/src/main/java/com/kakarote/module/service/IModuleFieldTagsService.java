package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleTagsBO;
import com.kakarote.module.entity.PO.ModuleFieldTags;

import java.util.List;

/**
 * @author zjj
 * @title: IModuleFieldTagsService
 * @description: 字段标签选项 服务接口
 * @date 2022/3/314:49
 */
public interface IModuleFieldTagsService extends BaseService<ModuleFieldTags> {

    /**
     * 获取字段的标签选项
     *
     * @param moduleId
     * @param fieldId
     * @param version
     * @return
     */
    List<ModuleTagsBO> queryTagList(Long moduleId, Long fieldId, Integer version);

    /**
     * 根据模块id和版本号获取标签字段的选项
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<ModuleFieldTags> getByModuleIdAndVersion(Long moduleId, Integer version);

}
