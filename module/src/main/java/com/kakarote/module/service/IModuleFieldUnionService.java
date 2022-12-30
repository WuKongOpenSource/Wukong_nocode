package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleFieldUnionSaveBO;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldUnion;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-08 14:24
 */
public interface IModuleFieldUnionService extends BaseService<ModuleFieldUnion> {

	List<ModuleFieldUnion> getByModuleIdAndVersion(Long moduleId, Integer version);

	/**
	 * 获取指定类型的字段关联配置
	 *
	 * @param moduleId
	 * @param version
	 * @param type
	 * @return
	 */
	List<ModuleFieldUnion> getByModuleIdAndVersion(Long moduleId, Integer version, Integer type);

	/**
	 * 查询数据关联字段信息
	 *
	 * @param targetModuleId 被关联的模块
	 * @param moduleId       有数据关联的module
	 * @return
	 */
	List<ModuleField> queryUnionList(Long targetModuleId, Long moduleId);
}
