package com.kakarote.module.service;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.CommonUnionConditionBO;
import com.kakarote.module.entity.BO.ConditionDataBO;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldUnionCondition;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-08 14:23
 */
public interface IModuleFieldUnionConditionService extends BaseService<ModuleFieldUnionCondition> {

	/**
	 * 获取模块字段值
	 *
	 * @param conditions 筛选条件
	 * @param dataBO     条件数据
	 * @param search
	 * @return
	 */
	BasePage<Map<String, Object>> queryModuleDataList(List<CommonUnionConditionBO> conditions, ConditionDataBO dataBO, SearchBO search);

	/**
	 * 数据关联结果查询
	 *
	 * @param searchBO
	 * @return
	 */
	BasePage<Map<String, Object>> searchDataUnionFieldData(ConditionSearchRequest searchBO);

	SearchEntityBO transConditionJsonToEntity(CommonUnionConditionBO unionCondition);

	List<ModuleFieldUnionCondition> getByModuleIdAndVersion(Long moduleId, Integer version);
}
