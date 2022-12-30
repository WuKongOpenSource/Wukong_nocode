package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.TimeValueUtil;
import com.kakarote.module.common.mq.ProducerUtil;
import com.kakarote.module.constant.*;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.proto.FieldDataSnapshotBuf;
import com.kakarote.module.mapper.FlowDataDealRecordMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 节点数据处理记录
 * @author zjj
 * @date 2021-07-12 10:36
 */
@Service
public class FlowDataDealRecordServiceImpl extends BaseServiceImpl<FlowDataDealRecordMapper, FlowDataDealRecord> implements IFlowDataDealRecordService, IFlowCommonService {

	@Autowired
	private IFlowService flowService;

	@Autowired
	private IFlowTimeLimitService timeLimitService;

	@Autowired
	private IModuleFieldDataProvider fieldDataProvider;

	@Autowired
	private IFlowExamineRecordService examineRecordService;

	@Autowired
	private ProducerUtil producerUtil;

	@Override
	public FlowDataDealRecord getMainByRecordIdAndFlowId(Long recordId, Long flowId) {
		return lambdaQuery().eq(FlowDataDealRecord::getRecordId, recordId).eq(FlowDataDealRecord::getFlowId, flowId)
				.eq(FlowDataDealRecord::getIsMain, true)
				.orderByDesc(FlowDataDealRecord::getCreateTime).one();
	}

	@Override
	public List<FlowDataDealRecord> getMainByRecordId(Long recordId) {
		return lambdaQuery().eq(FlowDataDealRecord::getRecordId, recordId)
				.eq(FlowDataDealRecord::getIsMain, true)
				.orderByDesc(FlowDataDealRecord::getCreateTime).list();
	}

	@Override
	public List<FlowDataDealRecord> saveRecords(Flow currentFlow, ExamineUserBO examineUserBO, Long recordId, Long currentUserId, Long dataId, String batchId) {
		List<FlowDataDealRecord> records = new ArrayList<>();
		// 角色审批
		if (ObjectUtil.isNotNull(examineUserBO.getRoleId()) && CollUtil.isEmpty(examineUserBO.getUserIds())) {
			FlowDataDealRecord dealRecord = new FlowDataDealRecord();
			dealRecord.setIsMain(false);
			dealRecord.setUserId(0L);
			dealRecord.setRoleId(examineUserBO.getRoleId());
			dealRecord.setCreateUserId(currentUserId);
			dealRecord.setRecordId(recordId);
			dealRecord.setType(examineUserBO.getType());
			dealRecord.setFlowId(currentFlow.getFlowId());
			dealRecord.setDataId(dataId);
			dealRecord.setCreateTime(DateUtil.date());
			dealRecord.setModuleId(currentFlow.getModuleId());
			dealRecord.setVersion(currentFlow.getVersion());
			dealRecord.setFlowType(currentFlow.getFlowType());
			dealRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
			dealRecord.setBatchId(batchId);
			save(dealRecord);
			records.add(dealRecord);
		}else {
			AtomicInteger atomicInteger = new AtomicInteger(0);
			for (Long userId : examineUserBO.getUserIds()) {
				FlowDataDealRecord dealRecord = new FlowDataDealRecord();
				dealRecord.setIsMain(false);
				dealRecord.setUserId(userId);
				dealRecord.setRoleId(0L);
				dealRecord.setSort(atomicInteger.getAndAdd(100));
				dealRecord.setCreateUserId(currentUserId);
				dealRecord.setRecordId(recordId);
				dealRecord.setType(examineUserBO.getType());
				// 1 依次审批 2 会签 3 或签
				if (ObjectUtil.equal(1, examineUserBO.getType())) {
					if (ObjectUtil.equal(0, dealRecord.getSort())) {
						dealRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
					} else {
						dealRecord.setFlowStatus(FlowStatusEnum.WAIT.getStatus());
					}
				} else {
					dealRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
				}
				dealRecord.setFlowId(currentFlow.getFlowId());
				dealRecord.setDataId(dataId);
				dealRecord.setCreateTime(DateUtil.date());
				dealRecord.setModuleId(currentFlow.getModuleId());
				dealRecord.setVersion(currentFlow.getVersion());
				dealRecord.setFlowType(currentFlow.getFlowType());
				dealRecord.setBatchId(batchId);
				save(dealRecord);
				records.add(dealRecord);
			}
		}
		return records;
	}

	@Override
	public FlowDataDealRecord getNext(FlowDataDealRecord record) {
		if (ObjectUtil.isNull(record.getParentId())) {
			return lambdaQuery()
					.eq(FlowDataDealRecord::getBatchId, record.getBatchId())
					.eq(FlowDataDealRecord::getIsMain, false)
					.in(FlowDataDealRecord::getFlowStatus,
							FlowStatusEnum.WAIT.getStatus(), FlowStatusEnum.DEALING.getStatus())
					.ne(FlowDataDealRecord::getId, record.getId())
					.orderByAsc(FlowDataDealRecord::getSort).one();
		} else {
			return lambdaQuery()
					.eq(FlowDataDealRecord::getBatchId, record.getBatchId())
					.eq(FlowDataDealRecord::getIsMain, false)
					.in(FlowDataDealRecord::getFlowStatus,
							FlowStatusEnum.WAIT.getStatus(), FlowStatusEnum.DEALING.getStatus())
					.eq(FlowDataDealRecord::getParentId, record.getParentId())
					.ne(FlowDataDealRecord::getId, record.getId())
					.orderByAsc(FlowDataDealRecord::getSort).one();
		}
	}

	@Override
	public void sendMessageForRecord(ModuleEntity module, Flow flow, Long dataId, String mainFieldValue, Long receiver, Integer status, Long createUserId) {
		// 发送消息
		MessageBO messageBO = new MessageBO();
		messageBO.setDataId(dataId);
		messageBO.setValue(mainFieldValue);
		messageBO.setModuleId(module.getModuleId());
		messageBO.setModuleName(module.getName());
		messageBO.setTypeId(flow.getFlowId());
		messageBO.setTypeName(flow.getFlowName());
		messageBO.setExtData(new JSONObject().fluentPut("entity", flow).toJSONString());
        messageBO.setType(1);
		messageBO.setReceivers(Collections.singletonList(receiver));
		messageBO.setCreateUserId(createUserId);
		messageBO.setStatus(status);
		IMessageService messageService = ApplicationContextHolder.getBean(IMessageService.class);
		messageService.sendMessage(messageBO);
	}

	private JSONArray snapshot2JsonObj(FieldDataSnapshotBuf.FieldDataSnapshot snapshot, Boolean sourceFlag, Integer flowType, FlowDealDetailQueryBO queryBO) {
		JSONArray dataList = new JSONArray();
		for (FieldDataSnapshotBuf.FieldDataSnapshot.Data data : snapshot.getFieldDataListList()) {
			JSONObject object = new JSONObject();
			object.putAll(data.getDataMap());
			Long moduleId = object.getLongValue("moduleId");
			moduleId = ObjectUtil.notEqual(moduleId, 0L) ? moduleId : queryBO.getModuleId();
			ModuleEntity module = ApplicationContextHolder.getBean(IModuleService.class).getNormal(moduleId);
			List<ModuleField> fields = ApplicationContextHolder.getBean(IModuleFieldService.class).getByModuleIdAndVersion(module.getModuleId(), module.getVersion(), null);
			for (ModuleField field : fields) {
				Object value = object.get(field.getFieldName());
				if (ObjectUtil.isNotNull(value)) {
					ModuleFieldEnum type = ModuleFieldEnum.parse(field.getType());
					String valueStr = parseValue2StringByType(type, value.toString(), sourceFlag, flowType);
					object.put(field.getFieldName(), valueStr);
				}
			}
			dataList.add(object);
		}
		return dataList;
	}

	@Override
	public JSONArray getFlowDealDetail(FlowDealDetailQueryBO queryBO) {
		JSONArray result = new JSONArray();
        Flow flow = flowService.getByFlowId(queryBO.getFlowId());
        if (ObjectUtil.isNull(flow)) {
            throw new BusinessException(ModuleCodeEnum.FLOW_NOT_FOUND);
        }
		FlowExamineRecord examineRecord = examineRecordService.lambdaQuery()
				.eq(FlowExamineRecord::getModuleId, queryBO.getModuleId())
				.eq(FlowExamineRecord::getFlowMetadataId, flow.getFlowMetadataId())
				.eq(FlowExamineRecord::getDataId, queryBO.getDataId())
				.orderByDesc(FlowExamineRecord::getCreateTime)
				.one();
		List<FlowDataDealRecord> records = lambdaQuery().eq(FlowDataDealRecord::getRecordId, examineRecord.getRecordId())
				.eq(FlowDataDealRecord::getModuleId, queryBO.getModuleId())
				.eq(FlowDataDealRecord::getDataId, queryBO.getDataId())
				.eq(FlowDataDealRecord::getFlowId, queryBO.getFlowId())
				.orderByAsc(FlowDataDealRecord::getSort).list();
		if (CollUtil.isEmpty(records)){
			return result;
		}
		List<FlowDataDealRecord> dealRecords = new ArrayList<>();
		if (ObjectUtil.equal(FlowTypeEnum.EXAMINE.getType(), flow.getFlowType())) {
			dealRecords = records.stream().filter(r -> !r.getIsMain()).collect(Collectors.toList());
		} else if (ObjectUtil.equal(FlowTypeEnum.FILL.getType(), flow.getFlowType())) {
			dealRecords = records.stream().filter(r -> !r.getIsMain()).collect(Collectors.toList());
		} else if (ObjectUtil.equal(FlowTypeEnum.COPY.getType(), flow.getFlowType())) {
			dealRecords = records.stream().filter(FlowDataDealRecord::getIsMain).collect(Collectors.toList());
		} else if (ObjectUtil.equal(FlowTypeEnum.SAVE.getType(), flow.getFlowType())) {
			dealRecords = records.stream().filter(FlowDataDealRecord::getIsMain).collect(Collectors.toList());
		} else if (ObjectUtil.equal(FlowTypeEnum.UPDATE.getType(), flow.getFlowType())) {
			dealRecords = records.stream().filter(FlowDataDealRecord::getIsMain).collect(Collectors.toList());
		}
        CollUtil.sort(dealRecords, Comparator.comparing(FlowDataDealRecord::getCreateTime));

		for (FlowDataDealRecord record : dealRecords) {
			JSONObject r = JSONObject.parseObject(JSON.toJSONString(record));
			r.put("user", UserCacheUtil.getSimpleUser(record.getUserId()));
			if (ObjectUtil.isNotNull(record.getSourceData())) {
				try {
					FieldDataSnapshotBuf.FieldDataSnapshot sourceSnapshot = FieldDataSnapshotBuf.FieldDataSnapshot.parseFrom(record.getSourceData());
					r.put("sourceData", snapshot2JsonObj(sourceSnapshot,true, flow.getFlowType(), queryBO) );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (ObjectUtil.isNotNull(record.getCurrentData())) {
				try {
					FieldDataSnapshotBuf.FieldDataSnapshot currentSnapshot = FieldDataSnapshotBuf.FieldDataSnapshot.parseFrom(record.getCurrentData());
					r.put("currentData", snapshot2JsonObj(currentSnapshot, false, flow.getFlowType(), queryBO));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result.add(r);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void transferFlow(TransferFlowBO transferFlowBO) {
        Flow flow = flowService.getByFlowId(transferFlowBO.getFlowId());
        if (ObjectUtil.isNull(flow)) {
            throw new BusinessException(ModuleCodeEnum.FLOW_NOT_FOUND);
        }
		ModuleEntity module = ModuleCacheUtil.getByIdAndVersion(flow.getModuleId(), flow.getVersion());
		FlowExamineRecord examineRecord = examineRecordService.lambdaQuery()
				.eq(FlowExamineRecord::getModuleId, transferFlowBO.getModuleId())
				.eq(FlowExamineRecord::getFlowMetadataId, flow.getFlowMetadataId())
				.eq(FlowExamineRecord::getDataId, transferFlowBO.getDataId())
				.orderByDesc(FlowExamineRecord::getCreateTime)
				.one();
		FlowTimeLimit timeLimit = timeLimitService.getByModuleIdAndFlowId(flow.getModuleId(), flow.getFlowId());

		FlowDataDealRecord record = lambdaQuery().eq(FlowDataDealRecord::getRecordId, examineRecord.getRecordId())
				.eq(FlowDataDealRecord::getModuleId, transferFlowBO.getModuleId())
				.eq(FlowDataDealRecord::getDataId, transferFlowBO.getDataId())
				.eq(FlowDataDealRecord::getFlowId, transferFlowBO.getFlowId())
				.eq(FlowDataDealRecord::getUserId, transferFlowBO.getFromUserId())
				.in(FlowDataDealRecord::getFlowStatus,
						FlowStatusEnum.WAIT.getStatus(), FlowStatusEnum.DEALING.getStatus())
				.eq(FlowDataDealRecord::getIsMain, false)
				.orderByDesc(FlowDataDealRecord::getCreateTime).one();
		if (ObjectUtil.isNull(record)) {
			return;
		}
		// 原有的负责人记录置为失效
		if (ObjectUtil.isNotNull(timeLimit) && ObjectUtil.equal(0, timeLimit.getOpenTimeLimit())) {
			record.setFlowStatus(FlowStatusEnum.INVALID.getStatus());
		}
        record.setRemark(transferFlowBO.getRemark());
        record.setInvalidType(1);
		updateById(record);
		List<FlowDataDealRecord> records = new ArrayList<>();
		AtomicInteger sort = new AtomicInteger(record.getSort());
		for (Long toUserId : transferFlowBO.getToUserIds()) {
			FlowDataDealRecord r = JSON.parseObject(JSON.toJSONString(record), FlowDataDealRecord.class);
			r.setId(null);
			r.setParentId(record.getId());
			r.setType(timeLimit.getExamineType());
			r.setUserId(toUserId);
			r.setCreateTime(DateUtil.date());
            r.setInvalidType(null);
            r.setRemark(null);
			r.setSort(sort.incrementAndGet());
			if (ObjectUtil.equal(1, r.getType())) {
				if (ObjectUtil.equal(record.getSort() + 1, r.getSort())) {
					r.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
				} else {
					r.setFlowStatus(FlowStatusEnum.WAIT.getStatus());
				}
			} else {
				r.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
			}
			records.add(r);
		}
		saveBatch(records);
		if (ObjectUtil.isNotNull(timeLimit)) {
			// 节点消息通知
			if (ObjectUtil.equal(1, timeLimit.getIsSendMessage())) {
				// 创建人+模块名+主字段+发送给您，请及时查看
				String mainFieldValue = fieldDataProvider.queryValue(record.getDataId(), module.getMainFieldId());
				MessageBO messageBO = new MessageBO();
				messageBO.setDataId(record.getDataId());
				messageBO.setValue(mainFieldValue);
				messageBO.setModuleId(record.getModuleId());
				messageBO.setModuleName(module.getName());
				messageBO.setTypeId(flow.getFlowId());
				messageBO.setTypeName(flow.getFlowName());
				messageBO.setExtData(new JSONObject().fluentPut("entity", flow).toJSONString());
				messageBO.setType(1);
				messageBO.setStatus(FlowStatusEnum.DEALING.getStatus());
				messageBO.setReceivers(transferFlowBO.getToUserIds());
				messageBO.setCreateUserId(record.getCreateUserId());
				IMessageService messageService = ApplicationContextHolder.getBean(IMessageService.class);
				messageService.sendMessage(messageBO);
			}
			// 节点限时处理
			if (ObjectUtil.equal(1, timeLimit.getOpenTimeLimit())) {
				Long delayTime = TimeValueUtil.parseTimeMillis(timeLimit.getTimeValue());
				MsgBodyBO msgBody = new MsgBodyBO();
				msgBody.setMsgTag(MessageTagEnum.EXAMINE_TIME_LIMIT);
				msgBody.setMsgKey(IdUtil.simpleUUID());
				msgBody.setModuleId(module.getModuleId());
				msgBody.setVersion(module.getVersion());
				msgBody.setDataId(record.getDataId());
				msgBody.setUserId(UserUtil.getUserId());
				msgBody.setFlowId(flow.getFlowId());
				msgBody.setRecordId(record.getRecordId());
				msgBody.setDelayTime(delayTime);
				producerUtil.sendMsgToTopicOne(msgBody);
			}
		}
	}

	@Override
	public List<FlowDataDealRecord> getMainByModuleIdAndDataId(Long moduleId, Long dataId) {
		return lambdaQuery()
				.eq(FlowDataDealRecord::getModuleId, moduleId)
				.eq(FlowDataDealRecord::getDataId, dataId)
				.orderByDesc(FlowDataDealRecord::getCreateTime)
				.list();
	}

	@Override
	public List<FlowDealDetailBO> getCustomButtonDealRecord(Long moduleId, Long dataId) {
		List<FlowDealDetailBO> result = new ArrayList<>();
		ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
		if (ObjectUtil.isNull(module)) {
			return result;
		}
		ICustomButtonService buttonService = ApplicationContextHolder.getBean(ICustomButtonService.class);
		List<CustomButton> buttons = buttonService.getByModuleIdAndVersion(moduleId, module.getVersion());
		if (CollUtil.isEmpty(buttons)) {
			return result;
		}
		List<FlowExamineRecord> examineRecords = examineRecordService.getCustomButtonRecordByModuleIdAndDataId(moduleId, dataId);
		Map<Long, FlowExamineRecord> buttonIdRecordMap = examineRecords.stream().collect(Collectors.toMap(FlowExamineRecord::getTypeId, Function.identity()));
		for (CustomButton button : buttons) {
			FlowDealDetailBO dealDetailBO = new FlowDealDetailBO();
			dealDetailBO.setCustomButton(button);
			FlowExamineRecord flowExamineRecord = buttonIdRecordMap.get(button.getButtonId());
			if (ObjectUtil.isNotNull(flowExamineRecord)) {
				dealDetailBO.setRecordId(flowExamineRecord.getRecordId());
				dealDetailBO.setDealTime(flowExamineRecord.getCreateTime());
				dealDetailBO.setUserId(flowExamineRecord.getCreateUserId());
				dealDetailBO.setUserName(UserCacheUtil.getUserName(flowExamineRecord.getCreateUserId()));
				dealDetailBO.setFlowStatus(flowExamineRecord.getExamineStatus());
			}
			result.add(dealDetailBO);
		}
		return result;
	}
}
