package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-08 17:35
 */
public interface IModuleFieldDataCommonService extends BaseService<ModuleFieldDataCommon> {

	ModuleFieldDataCommon getByDataId(Long dataId);

	List<ModuleFieldDataCommon> getByModuleId(Long moduleId, int offset, int limit);

	/**
	 * 根据批次删除导入的数据
	 *
	 * @param batchId batchId
	 */
    void revertImport(String batchId);
}
