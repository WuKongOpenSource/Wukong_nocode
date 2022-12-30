package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleOptionsBO;
import com.kakarote.module.entity.PO.ModuleFieldOptions;

import java.util.List;

/**
 * <p>
 * 字段选项表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-03-06
 */
public interface IModuleFieldOptionsService extends BaseService<ModuleFieldOptions> {

    /**
     * 查询字段选项信息
     *
     * @param moduleId
     * @param fieldId
     * @param version
     * @return
     */
    List<ModuleOptionsBO> queryOptionsList(Long moduleId, Long fieldId, Integer version);

	List<ModuleFieldOptions> getByModuleIdAndVersion(Long moduleId, Integer version);

}
