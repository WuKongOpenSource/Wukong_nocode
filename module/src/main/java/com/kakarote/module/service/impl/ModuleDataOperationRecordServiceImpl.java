package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.constant.ActionTypeEnum;

import com.kakarote.module.entity.BO.ModuleFieldValueBO;
import com.kakarote.module.entity.BO.ModuleFieldValueUpdateBO;
import com.kakarote.module.entity.PO.FlowDataDealRecord;
import com.kakarote.module.entity.PO.ModuleDataOperationRecord;
import com.kakarote.module.entity.VO.ModuleDataOperationRecordVO;
import com.kakarote.module.mapper.ModuleDataOperationRecordMapper;
import com.kakarote.module.service.IModuleDataOperationRecordService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 字段值操作记录
 * @author: zjj
 * @date: 2021-05-10 10:42
 */
@Service
public class ModuleDataOperationRecordServiceImpl extends BaseServiceImpl<ModuleDataOperationRecordMapper,
		ModuleDataOperationRecord> implements IModuleDataOperationRecordService {

	@Override
	public ModuleDataOperationRecord initEntity(Long moduleId, Integer version, Long dataId, String value,
												List<ModuleFieldValueBO> oldData,
												List<ModuleFieldValueBO> currentData,
												ActionTypeEnum actionType) {
		ModuleDataOperationRecord operationRecord = new ModuleDataOperationRecord();
		operationRecord.setDataId(dataId);
		operationRecord.setModuleId(moduleId);
		operationRecord.setVersion(version);
		operationRecord.setValue(value);
		operationRecord.setActionType(actionType.getCode());
		operationRecord.setCreateTime(DateUtil.date());
		operationRecord.setCreateUserId(UserUtil.getUserId());
		operationRecord.setRemarks(actionType.getMsg());
		if (CollUtil.isNotEmpty(oldData) && CollUtil.isNotEmpty(currentData)) {
			Map<Long, String> oldFieldIdValueMap = oldData.stream().collect(Collectors.toMap(ModuleFieldValueBO::getFieldId, ModuleFieldValueBO::getValue));
			List<ModuleFieldValueUpdateBO> fieldValueUpdateBOS = new ArrayList<>();
			for (ModuleFieldValueBO currentFieldValue : currentData) {
				// 系统字段不进行判断
				if (ObjectUtil.equal(0, currentFieldValue.getFieldType())) {
					continue;
				}
				String currentValue = currentFieldValue.getValue();
				String oldValue = MapUtil.getStr(oldFieldIdValueMap, currentFieldValue.getFieldId());
				if (!StrUtil.equals(currentValue, oldValue)) {
					ModuleFieldValueUpdateBO valueUpdateBO = BeanUtil.copyProperties(currentFieldValue, ModuleFieldValueUpdateBO.class);
					valueUpdateBO.setOldValue(oldValue);
					fieldValueUpdateBOS.add(valueUpdateBO);
				}
			}
			operationRecord.setExtData(JSON.toJSONString(fieldValueUpdateBOS));
		}
		return operationRecord;
	}

	@Override
	public ModuleDataOperationRecord initTeamUserEntity(Long moduleId, Integer version, Long dataId, String value, Long userId, ActionTypeEnum actionType) {
		ModuleDataOperationRecord operationRecord = this.initEntity(moduleId, version, dataId, value, null, null, actionType);
		operationRecord.setTeamUserId(userId);
		return operationRecord;
	}

	@Override
	public ModuleDataOperationRecord initTransferEntity(Long moduleId, Integer version, Long dataId, String value, Long fromUserId, Long toUserId, ActionTypeEnum actionType) {
		ModuleDataOperationRecord operationRecord = this.initEntity(moduleId, version, dataId, value, null, null,actionType);
		operationRecord.setFromUserId(fromUserId);
		operationRecord.setToUserId(toUserId);
		return operationRecord;
	}

	@Override
	public List<ModuleDataOperationRecordVO> queryRecord(Long moduleId, Long dataId) {
		List<ModuleDataOperationRecord> operationRecords = lambdaQuery()
				.eq(ModuleDataOperationRecord::getModuleId, moduleId)
				.eq(ModuleDataOperationRecord::getDataId, dataId)
				.orderByDesc(ModuleDataOperationRecord::getCreateTime)
				.list();
		List<ModuleDataOperationRecordVO> result = new ArrayList<>();
		for (ModuleDataOperationRecord operationRecord : operationRecords) {
			ModuleDataOperationRecordVO recordVO = JSON.parseObject(JSON.toJSONString(operationRecord), ModuleDataOperationRecordVO.class);
			if (ObjectUtil.isNotNull(operationRecord.getFromUserId())) {
				recordVO.setFromUser(UserCacheUtil.getSimpleUser(operationRecord.getFromUserId()));
			}
			if (ObjectUtil.isNotNull(operationRecord.getToUserId())) {
				recordVO.setToUser(UserCacheUtil.getSimpleUser(operationRecord.getToUserId()));
			}
			if (ObjectUtil.isNotNull(operationRecord.getTeamUserId())) {
				recordVO.setTeamUser(UserCacheUtil.getSimpleUser(operationRecord.getTeamUserId()));
			}
			if (ObjectUtil.isNotNull(operationRecord.getExamineRecordId())) {
				FlowDataDealRecord dealRecord = JSON.parseObject(operationRecord.getExtData(), FlowDataDealRecord.class);
				ModuleDataOperationRecordVO.ExamineRecord examineRecord = new ModuleDataOperationRecordVO.ExamineRecord();
				examineRecord.setRecordId(dealRecord.getRecordId());
				examineRecord.setCreateUser(UserCacheUtil.getSimpleUser(dealRecord.getUserId()));
				recordVO.setExamineRecord(examineRecord);
			}
			recordVO.setCreateUser(UserCacheUtil.getSimpleUser(operationRecord.getCreateUserId()));
			result.add(recordVO);
		}
		return result;
	}
}
