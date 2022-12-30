package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.ModuleFieldUnionBO;
import com.kakarote.module.entity.BO.ModuleFieldUnionConditionBO;
import com.kakarote.module.entity.BO.ModuleFieldUnionSaveBO;
import com.kakarote.module.entity.BO.SearchEntityBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldUnion;
import com.kakarote.module.entity.PO.ModuleFieldUnionCondition;
import com.kakarote.module.mapper.ModuleFieldUnionMapper;
import com.kakarote.module.service.IModuleFieldService;
import com.kakarote.module.service.IModuleFieldUnionConditionService;
import com.kakarote.module.service.IModuleFieldUnionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 字段关联
 * @author zjj
 * @date 2021-05-08 14:24
 */
@Service
public class ModuleFieldUnionServiceImpl extends BaseServiceImpl<ModuleFieldUnionMapper, ModuleFieldUnion> implements IModuleFieldUnionService {

	@Autowired
	private IModuleFieldService fieldService;

	@Override
	public List<ModuleFieldUnion> getByModuleIdAndVersion(Long moduleId, Integer version) {
		return lambdaQuery()
				.eq(ModuleFieldUnion::getModuleId, moduleId)
				.eq(ModuleFieldUnion::getVersion, version).list();
	}

	@Override
	public List<ModuleFieldUnion> getByModuleIdAndVersion(Long moduleId, Integer version, Integer type) {
		return lambdaQuery()
				.eq(ModuleFieldUnion::getModuleId, moduleId)
				.eq(ModuleFieldUnion::getVersion, version)
				.eq(ModuleFieldUnion::getType, type)
				.list();
	}

	@Override
	public List<ModuleField> queryUnionList(Long targetModuleId, Long moduleId) {
		ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
		List<ModuleFieldUnion> fieldUnions = lambdaQuery()
				.eq(ModuleFieldUnion::getModuleId, module.getModuleId())
				.eq(ModuleFieldUnion::getVersion, module.getVersion())
				.eq(ModuleFieldUnion::getTargetModuleId, targetModuleId)
				.eq(ModuleFieldUnion::getType, 1)
				.list();
		List<ModuleField> result = new ArrayList<>();
		if (CollUtil.isNotEmpty(fieldUnions)) {
			List<Long> unionFieldIds = fieldUnions.stream().map(ModuleFieldUnion::getRelateFieldId).collect(Collectors.toList());
			result = fieldService.lambdaQuery()
					.eq(ModuleField::getModuleId, module.getModuleId())
					.eq(ModuleField::getVersion, module.getVersion())
					.in(ModuleField::getFieldId, unionFieldIds)
					.list();
		}
		return result;
	}
}
