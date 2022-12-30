package com.kakarote.module.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.mapper.ModuleFieldDataCommonMapper;
import com.kakarote.module.service.IModuleFieldDataCommonService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-08 17:35
 */
@Service
public class ModuleFieldDataCommonServiceImpl extends BaseServiceImpl<ModuleFieldDataCommonMapper,
		ModuleFieldDataCommon> implements IModuleFieldDataCommonService {

	@Override
	public ModuleFieldDataCommon getByDataId(Long dataId) {
		ModuleFieldDataCommon dataCommon = baseMapper.getByDataId(dataId);
		if (ObjectUtil.isNull(dataCommon)) {
			return null;
		}
		dataCommon.setCreateUserName(dataCommon.getCreateUserId());
		dataCommon.setOwnerUserName(dataCommon.getOwnerUserId());
		return dataCommon;
	}

	@Override
	public List<ModuleFieldDataCommon> getByModuleId(Long moduleId, int offset, int limit) {
		return lambdaQuery().eq(ModuleFieldDataCommon::getModuleId, moduleId)
				.orderByAsc(ModuleFieldDataCommon::getDataId)
				.last("limit " + offset + "," + limit).list();
	}

	@Override
	public void revertImport(String batchId) {
		this.getBaseMapper().revertImport(batchId);
	}
}
