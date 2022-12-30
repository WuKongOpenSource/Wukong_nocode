package com.kakarote.module.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleFieldValueBO;
import com.kakarote.module.entity.PO.ModuleStatisticFieldUnion;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-17 17:45
 */
public interface IModuleStatisticFieldUnionService extends BaseService<ModuleStatisticFieldUnion> {

    /**
     * 获取统计字段值
     *
     * @param moduleId 当前模块 ID
     * @param version  版本
     * @param fieldId  当前统计字段
     * @param dataId   当前数据 ID
     * @return
     */
	ModuleFieldValueBO getStatisticFileValue(Long moduleId, Integer version, Long fieldId, Long dataId);


	/**
	 * 更新关联当前模块的统计字段值
	 *
	 * @param targetModuleId
	 * @param version
	 */
	void updateStatisticFieldValue(Long targetModuleId, Integer version);

    /**
     * 更新统计字段的值
     *
     * @param moduleId 模块ID
     * @param version  版本号
     * @param fieldId  统计字段ID
     * @param dataId   数据ID
     */
	void updateStatisticFieldValue(Long moduleId, Integer version, Long fieldId, Long dataId);

	List<ModuleStatisticFieldUnion> getByModuleIdAndVersion(Long moduleId, Integer version);
}
