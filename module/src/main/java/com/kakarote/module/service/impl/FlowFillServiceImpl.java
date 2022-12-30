package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.redis.Redis;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.common.TimeValueUtil;
import com.kakarote.module.common.mq.ProducerUtil;
import com.kakarote.module.constant.*;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.entity.proto.FieldDataSnapshotBuf;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @description: 流程填写节点 服务实现类
 * @author: zjj
 * @date: 2021-05-25 15:01
 */
@Service("fillService")
public class FlowFillServiceImpl implements IFlowFillService, IFlowTypeService {

	@Autowired
	private IFlowTimeLimitService timeLimitService;

	@Autowired
	private IFlowService flowService;

	@Autowired
	private IFlowProvider flowProvider;

	@Autowired
	private IModuleFieldService fieldService;

	@Autowired
	private IModuleService moduleService;

	@Autowired
	private IModuleFieldDataService fieldDataService;

	@Autowired
	private IModuleFieldDataProvider fieldDataProvider;

	@Autowired
	private IFlowFieldAuthService fieldAuthService;

	@Autowired
	private IFlowDataDealRecordService dealRecordService;

	@Autowired
	private IModuleFieldDataCommonService dataCommonService;

	@Autowired
	private ProducerUtil producerUtil;

	@Autowired
	private IFlowExamineRecordService recordService;

	@Autowired
	private IFlowExamineRecordService examineRecordService;

	@Autowired
	private Redis redis;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveFieldValue(FlowFillFieldDataSaveBO dataSaveBO) {
		// 同时只有一个人可以填写
		String key = String.format("%s-%s-%s-%s", FlowTypeEnum.FILL.name(), dataSaveBO.getModuleId(), dataSaveBO.getFlowId(), dataSaveBO.getDataId());
        if (!redis.setNx(key, 10L, 1)) {
            throw new BusinessException(ModuleCodeEnum.SOME_ONE_IS_FILLING_FIELD_DATA);
        }
        Flow flow = flowService.getByFlowId(dataSaveBO.getFlowId());
        if (ObjectUtil.isNull(flow)) {
            throw new BusinessException(ModuleCodeEnum.FLOW_NOT_FOUND);
        }
		// 获取当前数据的审批记录
		FlowExamineRecord examineRecord = examineRecordService.lambdaQuery()
				.eq(FlowExamineRecord::getModuleId, dataSaveBO.getModuleId())
				.eq(FlowExamineRecord::getFlowMetadataId, flow.getFlowMetadataId())
				.eq(FlowExamineRecord::getDataId, dataSaveBO.getDataId())
				.orderByDesc(FlowExamineRecord::getCreateTime)
				.one();
		// 获取节点的主记录
		FlowDataDealRecord mainDataDealRecord = dealRecordService.getMainByRecordIdAndFlowId(examineRecord.getRecordId(), dataSaveBO.getFlowId());
		// 如果节点已处理，直接返回
		if (ObjectUtil.equal(FlowStatusEnum.PASS.getStatus(), mainDataDealRecord.getFlowStatus())) {
			return;
		}
		ModuleEntity module = moduleService.getByModuleIdAndVersion(mainDataDealRecord.getModuleId(), mainDataDealRecord.getVersion());
		// 模块字段
		List<ModuleField> moduleFields = fieldService.getByModuleIdAndVersion(examineRecord.getModuleId(), examineRecord.getVersion(), null);
		Map<Long, String> fieldIdNameMap = moduleFields.stream().collect(Collectors.toMap(ModuleField::getFieldId,
				ModuleField::getFieldName));

		// 获取模块主字段值
		String mainFieldValue = fieldDataService.queryMainFieldValue(mainDataDealRecord.getDataId());
		// 获取当前用户的处理记录
		FlowDataDealRecord dealRecord = dealRecordService.lambdaQuery()
				.eq(FlowDataDealRecord::getRecordId, examineRecord.getRecordId())
				.eq(FlowDataDealRecord::getDataId, examineRecord.getDataId())
				.eq(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.DEALING.getStatus())
				.eq(FlowDataDealRecord::getUserId, UserUtil.getUserId())
				.orderByAsc(FlowDataDealRecord::getSort).one();
		if (ObjectUtil.isNull(dealRecord)) {
			return;
		}
		// 填写节点的审批批注
		dealRecord.setRemark(dataSaveBO.getRemarks());
		// 用户填写的数据
		Map<String, Object> filledData = new HashMap<>();
		// 字段ID
		List<Long> fieldIds = JSON.parseArray(flow.getFieldId(), Long.class);
		if (CollUtil.isNotEmpty(fieldIds)) {
			// 用戶填写前的数据存为快照
			Map<String, Object> dataMap = fieldDataProvider.queryFieldNameDataMap(dataSaveBO.getDataId());
			FieldDataSnapshotBuf.FieldDataSnapshot sourceSnapshot = buildSnapshot(Collections.singletonList(dataMap));
			dealRecord.setSourceData(sourceSnapshot.toByteArray());
			// 保存字段值
			List<ModuleFieldData> fieldDataList = dataSaveBO.getFieldDataList();
			fieldDataList = fieldDataList.stream().filter(d -> fieldIds.contains(d.getFieldId())).collect(Collectors.toList());

			// 先删除要填写字段的已有的数据
			fieldDataService.lambdaUpdate()
					.eq(ModuleFieldData::getDataId, dataSaveBO.getDataId())
					.in(ModuleFieldData::getFieldId, fieldIds).remove();
			for (ModuleFieldData fieldData : fieldDataList) {
				fieldData.setDataId(dataSaveBO.getDataId());
				fieldData.setModuleId(dataSaveBO.getModuleId());
				fieldData.setCreateTime(DateUtil.date());
				fieldData.setVersion(module.getVersion());

				filledData.put(MapUtil.getStr(fieldIdNameMap, fieldData.getFieldId()), fieldData.getValue());
			}
			fieldDataService.saveOrUpdateBatch(fieldDataList);
			savePage(mainDataDealRecord.getDataId(), mainDataDealRecord.getModuleId(), mainDataDealRecord.getVersion());
			// 用户填写的数据存为快照
			FieldDataSnapshotBuf.FieldDataSnapshot currentSnapshot = buildSnapshot(Collections.singletonList(filledData));
			dealRecord.setCurrentData(currentSnapshot.toByteArray());
		}

		// 转交过来的填写
		if (ObjectUtil.isNotNull(dealRecord.getParentId())) {
			// 1 依次审批 2 会签 3 或签
			if (ObjectUtil.equal(1, dealRecord.getType())) {
				dealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
				dealRecord.setUpdateTime(DateUtil.date());
				dealRecordService.updateById(dealRecord);
				FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
				if (ObjectUtil.isNull(nextRecord)) {
					mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
					dealRecordService.updateById(mainDataDealRecord);
				} else {
					// 转交的审批
					if (ObjectUtil.isNotNull(nextRecord.getParentId())) {
						nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
						dealRecordService.updateById(nextRecord);
						// 通知下个待填写的用户
						dealRecordService.sendMessageForRecord(
								module, flow, mainDataDealRecord.getDataId(), mainFieldValue,
								nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(),
								examineRecord.getCreateUserId());
						return;
					} else {
						// 1 依次
						if (ObjectUtil.equal(1, nextRecord.getType())) {
							nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
							dealRecordService.updateById(nextRecord);
							// 通知下个待填写的用户
							dealRecordService.sendMessageForRecord(
									module, flow, mainDataDealRecord.getDataId(), mainFieldValue,
									nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(),
									examineRecord.getCreateUserId());
						}
						// 2 会签
						else if (ObjectUtil.equal(2, nextRecord.getType())) {
							return;
						}
						// 3 或签
						else {
							mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
							dealRecordService.updateById(mainDataDealRecord);
						}
					}
				}
			}
			// 2 会签
			else if (ObjectUtil.equal(2, dealRecord.getType())) {
				dealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
				dealRecord.setUpdateTime(DateUtil.date());
				dealRecordService.updateById(dealRecord);
				FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
				if (ObjectUtil.isNull(nextRecord)) {
					mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
					dealRecordService.updateById(mainDataDealRecord);
					return;
				} else {
					// 转交的审批
					if (ObjectUtil.isNotNull(nextRecord.getParentId())) {
						return;
					} else {
						// 1 依次审批
						if (ObjectUtil.equal(1, nextRecord.getType())) {
							nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
							dealRecordService.updateById(nextRecord);
							// 通知下个待审批的用户
							dealRecordService.sendMessageForRecord(module, flow, mainDataDealRecord.getDataId(), mainFieldValue,
									nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(), examineRecord.getCreateUserId());
							return;
						}
						// 2 会签
						else if (ObjectUtil.equal(2, nextRecord.getType())) {
							return;
						}
						// 3 或签
						else {
							mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
							dealRecordService.updateById(mainDataDealRecord);
						}
					}
				}
			}
			// 3 或签
			else {
				dealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
				dealRecord.setUpdateTime(DateUtil.date());
				dealRecordService.updateById(dealRecord);
				dealRecordService.lambdaUpdate()
						.set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.INVALID.getStatus())
						.set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
						.ne(FlowDataDealRecord::getId, dealRecord.getId())
						.eq(FlowDataDealRecord::getBatchId, dealRecord.getBatchId())
						.update();
				FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
				if (ObjectUtil.isNull(nextRecord)) {
					mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
					dealRecordService.updateById(mainDataDealRecord);
				} else {
					// 1 依次
					if (ObjectUtil.equal(1, nextRecord.getType())) {
						nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
						dealRecordService.updateById(nextRecord);
						dealRecordService.sendMessageForRecord(module, flow, mainDataDealRecord.getDataId(), mainFieldValue,
								nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(), examineRecord.getCreateUserId());
						return;
					}
					// 2 会签
					else if (ObjectUtil.equal(2, nextRecord.getType()))  {
						return;
					}
					// 3或签
					else {
						dealRecordService.lambdaUpdate()
								.set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.PASS.getStatus())
								.set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
								.eq(FlowDataDealRecord::getBatchId, dealRecord.getBatchId())
								.update();
						mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
						dealRecordService.updateById(mainDataDealRecord);
					}
				}
			}
		}
		// 直接的填写
		else {
			// 1 依次审批 2 会签 3 或签
			if (ObjectUtil.equal(1, dealRecord.getType())) {
				dealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
				dealRecord.setUpdateTime(DateUtil.date());
				dealRecordService.updateById(dealRecord);
				FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
				if (ObjectUtil.isNull(nextRecord)) {
					mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
					dealRecordService.updateById(mainDataDealRecord);
				} else {
					nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
					dealRecordService.updateById(nextRecord);
					// 通知下个待填写的用户
					dealRecordService.sendMessageForRecord(
							module, flow, mainDataDealRecord.getDataId(), mainFieldValue,
							nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(),
							examineRecord.getCreateUserId());
					return;
				}
			}
			// 2 会签
			else if (ObjectUtil.equal(2, dealRecord.getType())) {
				dealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
				dealRecord.setUpdateTime(DateUtil.date());
				dealRecordService.updateById(dealRecord);
				FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
				if (ObjectUtil.isNull(nextRecord)) {
					mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
					dealRecordService.updateById(mainDataDealRecord);
					return;
				} else {
					// 转交的审批
					if (ObjectUtil.isNotNull(nextRecord.getParentId())) {
						// 1 依次审批 2 会签 3 或签
						if (ObjectUtil.equal(1, nextRecord.getType())) {
							nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
							dealRecordService.updateById(nextRecord);
							// 通知下个待审批的用户
							dealRecordService.sendMessageForRecord(
									module, flow, mainDataDealRecord.getDataId(), mainFieldValue,
									nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(),
									examineRecord.getCreateUserId());
							return;
						}
					} else {
						return;
					}
				}
			}
			// 3 或签
			else {
				dealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
				dealRecord.setUpdateTime(DateUtil.date());
				dealRecordService.updateById(dealRecord);
				// 其他或签废弃
				dealRecordService.lambdaUpdate()
						.set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.INVALID.getStatus())
						.set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
						.ne(FlowDataDealRecord::getId, dealRecord.getId())
						.eq(FlowDataDealRecord::getBatchId, dealRecord.getBatchId())
						.update();
				mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
				dealRecordService.updateById(mainDataDealRecord);
			}
		}

		// 更新数据的流程状态
		ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(dataSaveBO.getDataId());
		dataCommon.setFlowStatus(FlowStatusEnum.PASS.getStatus());
		dataCommon.setType(1);
		dataCommon.setCurrentFlowId(dataSaveBO.getFlowId());
		dataCommon.setUpdateTime(DateUtil.date());
		dataCommonService.updateById(dataCommon);
        savePage(mainDataDealRecord.getDataId(), mainDataDealRecord.getModuleId(), mainDataDealRecord.getVersion());

		// 获取当前节点的下一个节点，继续处理数据
		Flow nextFlow = flowProvider.getNextOrUpperNextFlow(flow);
		if (ObjectUtil.isNull(nextFlow)) {
			examineRecord.setUpdateTime(DateUtil.date());
			examineRecord.setUpdateUserId(UserUtil.getUserId());
			examineRecord.setExamineStatus(FlowStatusEnum.PASS.getStatus());
			examineRecordService.updateById(examineRecord);
			return;
		}
		// 发送MQ消息
		MsgBodyBO msgBody = new MsgBodyBO();
		msgBody.setMsgKey(IdUtil.simpleUUID());
		msgBody.setModuleId(examineRecord.getModuleId());
		msgBody.setVersion(examineRecord.getVersion());
		msgBody.setRecordId(examineRecord.getRecordId());
		msgBody.setFlowId(nextFlow.getFlowId());
		msgBody.setDataId(dataSaveBO.getDataId());
		msgBody.setUserId(examineRecord.getCreateUserId());
		msgBody.setMsgTag(MessageTagEnum.DEAL_FLOW);
		producerUtil.sendMsgToTopicOne(msgBody);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void dealTimeFlow(MsgBodyBO bodyBO) {
		FlowDataDealRecord mainDataDealRecord = dealRecordService.getMainByRecordIdAndFlowId(bodyBO.getRecordId(), bodyBO.getFlowId());
		// 如果节点数据已处理，直接返回
		if (FlowStatusEnum.stopFlow(mainDataDealRecord.getFlowStatus())) {
			return;
		}
		Flow flow = flowService.getByFlowId(mainDataDealRecord.getFlowId(), mainDataDealRecord.getVersion());
		FlowTimeLimit timeLimit = timeLimitService.getByModuleIdAndFlowId(bodyBO.getModuleId(), bodyBO.getFlowId());
		// 创建人+模块名+主字段+发送给您，请及时查看
		ModuleEntity module = moduleService.getByModuleIdAndVersion(mainDataDealRecord.getModuleId(), mainDataDealRecord.getVersion());
		String mainFieldValue = fieldDataProvider.queryValue(mainDataDealRecord.getDataId(), module.getMainFieldId());
		// 自动同意
		if (ObjectUtil.equal(3, timeLimit.getOvertimeType())) {
			// 1 依次审批 2 会签 3 或签
			if (ObjectUtil.equal(1, mainDataDealRecord.getType())) {
				FlowDataDealRecord dealRecord = dealRecordService.lambdaQuery()
						.eq(FlowDataDealRecord::getRecordId, bodyBO.getRecordId())
						.eq(FlowDataDealRecord::getDataId, bodyBO.getDataId())
						.eq(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.DEALING.getStatus())
						.orderByAsc(FlowDataDealRecord::getSort).one();
				if (ObjectUtil.isNull(dealRecord)) {
					return;
				}
				dealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
				dealRecord.setOvertimeType(timeLimit.getOvertimeType());
				dealRecord.setTimeValue(timeLimit.getTimeValue());
				dealRecord.setUpdateTime(DateUtil.date());
				dealRecordService.updateById(dealRecord);
				FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
				if (ObjectUtil.isNotNull(nextRecord)) {
					nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
					dealRecordService.updateById(nextRecord);
					// 通知下个待填写的用户
					dealRecordService.sendMessageForRecord(module, flow, bodyBO.getDataId(), mainFieldValue,
							nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(), mainDataDealRecord.getCreateUserId());
					// 发送延时消息
					Long delayTime = TimeValueUtil.parseTimeMillis(timeLimit.getTimeValue());
					MsgBodyBO msgBody = new MsgBodyBO();
					msgBody.setMsgTag(MessageTagEnum.FILL_TIME_LIMIT);
					msgBody.setMsgKey(IdUtil.simpleUUID());
					msgBody.setModuleId(module.getModuleId());
					msgBody.setVersion(module.getVersion());
					msgBody.setDataId(mainDataDealRecord.getDataId());
					msgBody.setUserId(UserUtil.getUserId());
					msgBody.setFlowId(mainDataDealRecord.getFlowId());
					msgBody.setRecordId(mainDataDealRecord.getRecordId());
					msgBody.setDelayTime(delayTime);
					producerUtil.sendMsgToTopicOne(msgBody);
					return;
				}
			} else {
				dealRecordService.lambdaUpdate()
						.set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
						.set(FlowDataDealRecord::getTimeValue, timeLimit.getTimeValue())
						.set(FlowDataDealRecord::getOvertimeType, timeLimit.getOvertimeType())
						.set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.PASS.getStatus())
						.ne(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.PASS.getStatus())
						.eq(FlowDataDealRecord::getBatchId, mainDataDealRecord.getBatchId())
						.update();
			}

			mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
			mainDataDealRecord.setUpdateTime(DateUtil.date());
			dealRecordService.updateById(mainDataDealRecord);
			// 获取当前节点的下一个节点，继续处理数据
			Flow nextFlow = flowProvider.getNextOrUpperNextFlow(flow);
			if (ObjectUtil.isNull(nextFlow)) {
				return;
			}
			FlowExamineRecord record = recordService.getById(mainDataDealRecord.getRecordId());
			FlowTypeEnum flowTypeEnum = FlowTypeEnum.parse(nextFlow.getFlowType());
			IFlowTypeService flowTypeService = ApplicationContextHolder.getBean(flowTypeEnum.getServiceName());
			flowTypeService.dealData(record, nextFlow.getFlowId());
			return;
		}

		// 节点负责人ID
		ExamineUserBO userBO = queryFillUser(flow, mainDataDealRecord.getCreateUserId(), mainDataDealRecord.getRecordId());
		// 自动提醒
		if (ObjectUtil.equal(1, timeLimit.getOvertimeType())) {
			// 发送超时提醒消息
			MessageBO messageBO = new MessageBO();
			messageBO.setDataId(mainDataDealRecord.getDataId());
			messageBO.setValue(mainFieldValue);
			messageBO.setModuleId(module.getModuleId());
			messageBO.setModuleName(module.getName());
			messageBO.setTypeId(flow.getFlowId());
			messageBO.setTypeName(flow.getFlowName());
			messageBO.setExtData(new JSONObject().fluentPut("entity", flow).toJSONString());
            messageBO.setType(1);
			messageBO.setTimeValue(timeLimit.getTimeValue());
			messageBO.setReceivers(userBO.getUserIds());
			messageBO.setCreateUserId(mainDataDealRecord.getCreateUserId());
			IMessageService messageService = ApplicationContextHolder.getBean(IMessageService.class);
			messageService.sendMessage(messageBO);
		}
		// 自动转交
		else if (ObjectUtil.equal(2, timeLimit.getOvertimeType())) {
			FlowDataDealRecord dealRecord = dealRecordService.lambdaQuery()
					.eq(FlowDataDealRecord::getRecordId, bodyBO.getRecordId())
					.eq(FlowDataDealRecord::getDataId, bodyBO.getDataId())
					.eq(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.DEALING.getStatus())
					.orderByAsc(FlowDataDealRecord::getSort).one();
			if (ObjectUtil.isNull(dealRecord)) {
				return;
			}
			// 变更负责人
			List<Long> userIds = JSON.parseArray(timeLimit.getUserIds(), Long.class);
			// 原有的负责人记录置为失效
			dealRecordService.lambdaUpdate()
					.set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.INVALID.getStatus())
					.set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
					.set(FlowDataDealRecord::getTimeValue, timeLimit.getTimeValue())
					.set(FlowDataDealRecord::getOvertimeType, timeLimit.getOvertimeType())
					.eq(FlowDataDealRecord::getRecordId, mainDataDealRecord.getRecordId())
					.eq(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.DEALING.getStatus())
					.eq(FlowDataDealRecord::getIsMain, false)
					.eq(FlowDataDealRecord::getFlowId,mainDataDealRecord.getFlowId()).update();
			// 负责人记录
			Date currentDate = DateUtil.date();
			String batchId = IdUtil.simpleUUID();
			AtomicInteger atomicInteger = new AtomicInteger(dealRecord.getSort() + 1);
			List<FlowDataDealRecord> records = new ArrayList<>();
			for (Long userId : userIds) {
				FlowDataDealRecord flowDataDealRecord = new FlowDataDealRecord();
				flowDataDealRecord.setIsMain(false);
				flowDataDealRecord.setUserId(userId);
				flowDataDealRecord.setCreateUserId(mainDataDealRecord.getCreateUserId());
				flowDataDealRecord.setRecordId(mainDataDealRecord.getRecordId());
				flowDataDealRecord.setVersion(mainDataDealRecord.getVersion());
				flowDataDealRecord.setFlowId(mainDataDealRecord.getFlowId());
				flowDataDealRecord.setDataId(mainDataDealRecord.getDataId());
				flowDataDealRecord.setType(timeLimit.getExamineType());
				flowDataDealRecord.setOvertimeType(timeLimit.getOvertimeType());
				flowDataDealRecord.setTimeValue(timeLimit.getTimeValue());
				flowDataDealRecord.setCreateTime(currentDate);
				flowDataDealRecord.setModuleId(mainDataDealRecord.getModuleId());
				flowDataDealRecord.setFlowType(FlowTypeEnum.FILL.getType());
				flowDataDealRecord.setFlowStatus(FlowStatusEnum.WAIT.getStatus());
				flowDataDealRecord.setRoleId(0L);
				flowDataDealRecord.setBatchId(batchId);
				flowDataDealRecord.setSort(atomicInteger.getAndIncrement());
				// 1 依次审批 2 会签 3 或签
				if (ObjectUtil.equal(1, timeLimit.getExamineType())) {
					if (ObjectUtil.equal(0, flowDataDealRecord.getSort())) {
						flowDataDealRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
					} else {
						flowDataDealRecord.setFlowStatus(FlowStatusEnum.WAIT.getStatus());
					}
				} else {
					flowDataDealRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
				}
				dealRecordService.save(flowDataDealRecord);
				records.add(flowDataDealRecord);
			}
			// 发送消息
			List<Long> receivers = records.stream()
					.filter(r -> ObjectUtil.equal(FlowStatusEnum.DEALING.getStatus(), r.getFlowStatus()))
					.map(FlowDataDealRecord::getUserId).collect(Collectors.toList());
			MessageBO messageBO = new MessageBO();
			messageBO.setDataId(mainDataDealRecord.getDataId());
			messageBO.setValue(mainFieldValue);
			messageBO.setModuleId(module.getModuleId());
			messageBO.setModuleName(module.getName());
			messageBO.setTypeId(flow.getFlowId());
			messageBO.setTypeName(flow.getFlowName());
			messageBO.setExtData(new JSONObject().fluentPut("entity", flow).toJSONString());
			messageBO.setType(1);
			messageBO.setReceivers(receivers);
			messageBO.setCreateUserId(mainDataDealRecord.getCreateUserId());
			messageBO.setStatus(FlowStatusEnum.DEALING.getStatus());
			IMessageService messageService = ApplicationContextHolder.getBean(IMessageService.class);
			messageService.sendMessage(messageBO);
		}
	}

	@Override
	public void queryExamineData(Map<String, Object> map, String batchId) {
		for (ExamineTypeEnum typeEnum : ExamineTypeEnum.values()) {
			if (Arrays.asList(ExamineTypeEnum.MANAGER, ExamineTypeEnum.NULL).contains(typeEnum)) {
				continue;
			}
			IFlowExamineTypeService examineTypeService = ApplicationContextHolder.getBean(typeEnum.getServiceName());
			examineTypeService.queryExamineData(map, batchId);
		}
	}

	@Override
	public FlowVO createFlowInfo(Map<String, Object> map, Flow flow, List<UserInfo> userInfos, Long ownerUserId) {
		ExamineTypeEnum examineType = ExamineTypeEnum.parse(flow.getType());
		IFlowExamineTypeService examineTypeService = ApplicationContextHolder.getBean(examineType.getServiceName());
		FlowVO flowVO = examineTypeService.createFlowInfo(map, flow, userInfos, ownerUserId);
		FlowTimeLimit timeLimit = timeLimitService.getByModuleIdAndFlowId(flow.getModuleId(), flow.getFlowId());
		if (ObjectUtil.isNotNull(timeLimit)) {
			FlowVO.FlowTimeLimitConfig timeLimitConfig = BeanUtil.copyProperties(timeLimit, FlowVO.FlowTimeLimitConfig.class);
			timeLimitConfig.setTransferUsers(searchUsers(userInfos, JSON.parseArray(timeLimit.getTransferUserIds(), Long.class)));
			timeLimitConfig.setUsers(searchUsers(userInfos, JSON.parseArray(timeLimit.getUserIds(), Long.class)));
			flowVO.setTimeLimitConfig(timeLimitConfig);
		}
		FlowVO.FlowFillData data = JSON.parseObject(flowVO.getData().toJSONString(), FlowVO.FlowFillData.class);
		data.setFieldIds(JSON.parseArray(flow.getFieldId(), Long.class));
		flowVO.setData(JSON.parseObject(JSON.toJSONString(data)));
		return flowVO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void dealData(FlowExamineRecord record, Long flowId) {
		Flow currentFlow = flowService.getByFlowId(flowId, record.getVersion());
		FlowDataDealRecord dataDealRecord = dealRecordService.getMainByRecordIdAndFlowId(record.getRecordId(), flowId);
		// 当前节点未处理，直接结束不执行下一个节点
		if (ObjectUtil.isNull(dataDealRecord)) {
			ModuleEntity module = moduleService.getByModuleIdAndVersion(record.getModuleId(), record.getVersion());
			String batchId = IdUtil.simpleUUID();

			// 节点负责人ID
			ExamineUserBO userBO = queryFillUser(currentFlow, record.getCreateUserId(), record.getRecordId());
			List<FlowDataDealRecord> records = dealRecordService.saveRecords(currentFlow, userBO, record.getRecordId(), record.getCreateUserId(), record.getDataId(), batchId);
			List<Long> userIds = records.stream()
					.filter(r -> ObjectUtil.equal(FlowStatusEnum.DEALING.getStatus(), r.getFlowStatus()))
					.map(FlowDataDealRecord::getUserId).collect(Collectors.toList());
			// 保存待办信息
			List<ModuleFieldData> fieldDataList = fieldDataService.queryFieldData(record.getDataId());
			// 获取字段授权配置
			FlowFieldAuth flowFieldAuth = fieldAuthService.getByModuleIdAndFlowId(module.getModuleId(), module.getVersion(), flowId);
			if (CollUtil.isNotEmpty(userIds)) {
				List<ToDo> toDoList = new ArrayList<>();
				for (Long receiver : userIds) {
					ToDo toDo = new ToDo();
					toDo.setApplicationId(module.getApplicationId());
					toDo.setModuleId(module.getModuleId());
					toDo.setModuleName(module.getName());
					if (ObjectUtil.equal(FlowMetadataTypeEnum.CUSTOM_BUTTON.getType(), record.getFlowMetadataType())) {
						toDo.setObjectType(FlowMetadataTypeEnum.CUSTOM_BUTTON.getType());
						toDo.setObjectId(record.getTypeId());
					} else {
						toDo.setObjectType(FlowMetadataTypeEnum.SYSTEM.getType());
					}
					toDo.setRecordId(record.getRecordId());
					toDo.setVersion(record.getVersion());
					toDo.setDataId(record.getDataId());
					toDo.setFieldValue(JSON.toJSONString(fieldDataList));
					if (ObjectUtil.isNotNull(flowFieldAuth)) {
						toDo.setFieldAuth(flowFieldAuth.getAuth());
					}
					toDo.setType(0);
					toDo.setFlowType(FlowTypeEnum.FILL.getType());
					toDo.setTypeId(currentFlow.getFlowId());
					toDo.setTypeName(currentFlow.getFlowName());
					toDo.setCreateUserId(record.getCreateUserId());
					toDo.setCreateTime(DateUtil.date());
					toDo.setOwnerUserId(receiver);
					toDoList.add(toDo);
				}
				IToDoService toDoService = ApplicationContextHolder.getBean(IToDoService.class);
				toDoService.saveBatch(toDoList);
			}
			// 限时处理
			FlowTimeLimit timeLimit = timeLimitService.getByModuleIdAndFlowId(record.getModuleId(), flowId);
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
					messageBO.setTypeId(flowId);
					messageBO.setTypeName(currentFlow.getFlowName());
					messageBO.setExtData(new JSONObject().fluentPut("entity", currentFlow).toJSONString());
                    messageBO.setType(1);
					messageBO.setStatus(FlowStatusEnum.DEALING.getStatus());
					messageBO.setReceivers(userIds);
					messageBO.setCreateUserId(record.getCreateUserId());
					IMessageService messageService = ApplicationContextHolder.getBean(IMessageService.class);
					messageService.sendMessage(messageBO);
				}
				// 节点限时处理
				if (ObjectUtil.equal(1, timeLimit.getOpenTimeLimit())) {
					Long delayTime = TimeValueUtil.parseTimeMillis(timeLimit.getTimeValue());
					MsgBodyBO msgBody = new MsgBodyBO();
					msgBody.setMsgTag(MessageTagEnum.FILL_TIME_LIMIT);
					msgBody.setMsgKey(IdUtil.simpleUUID());
					msgBody.setModuleId(module.getModuleId());
					msgBody.setDataId(record.getDataId());
					msgBody.setUserId(UserUtil.getUserId());
					msgBody.setFlowId(flowId);
					msgBody.setRecordId(record.getRecordId());
					msgBody.setDelayTime(delayTime);
					producerUtil.sendMsgToTopicOne(msgBody);
				}
			}

			// 保存主记录
			dataDealRecord = new FlowDataDealRecord();
			dataDealRecord.setIsMain(true);
			dataDealRecord.setCreateUserId(record.getCreateUserId());
			dataDealRecord.setRecordId(record.getRecordId());
			dataDealRecord.setFlowId(currentFlow.getFlowId());
			dataDealRecord.setDataId(record.getDataId());
			dataDealRecord.setCreateTime(DateUtil.date());
			dataDealRecord.setModuleId(record.getModuleId());
			dataDealRecord.setVersion(record.getVersion());
			dataDealRecord.setType(currentFlow.getType());
			dataDealRecord.setFlowType(FlowTypeEnum.FILL.getType());
			dataDealRecord.setFlowStatus(FlowStatusEnum.WAIT.getStatus());
			dataDealRecord.setBatchId(batchId);
			dealRecordService.save(dataDealRecord);

			// 更新数据的流程状态
			ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(record.getDataId());
			dataCommon.setFlowStatus(FlowStatusEnum.WAIT.getStatus());
			dataCommon.setFlowType(FlowTypeEnum.FILL.getType());
			dataCommon.setType(1);
			dataCommon.setCurrentFlowId(flowId);
			dataCommon.setUpdateTime(DateUtil.date());
			dataCommonService.updateById(dataCommon);
			savePage(record.getDataId(), record.getModuleId(), record.getVersion());
			return;
		}
		// 待填写
		else if (ObjectUtil.equal(FlowStatusEnum.WAIT.getStatus(), dataDealRecord.getFlowStatus())) {
			return;
		}
		// 获取当前节点的下一个节点，继续处理数据
		Flow nextFlow = flowProvider.getNextOrUpperNextFlow(currentFlow);
		if (ObjectUtil.isNull(nextFlow)) {
			updateExamineStatus(record, currentFlow, FlowStatusEnum.PASS);
			return;
		}
		FlowTypeEnum flowTypeEnum = FlowTypeEnum.parse(nextFlow.getFlowType());
		IFlowTypeService flowTypeService = ApplicationContextHolder.getBean(flowTypeEnum.getServiceName());
		flowTypeService.dealData(record, nextFlow.getFlowId());
	}

	private ExamineUserBO queryFillUser(Flow fillFlow, Long createUserId, Long recordId) {
		ExamineTypeEnum examineTypeEnum = ExamineTypeEnum.parse(fillFlow.getType());
		IFlowExamineTypeService examineTypeService = ApplicationContextHolder.getBean(examineTypeEnum.getServiceName());
		ExamineUserQueryBO queryBO = new ExamineUserQueryBO();
		queryBO.setFlow(fillFlow);
		queryBO.setCreateUserId(createUserId);
		queryBO.setRecordId(recordId);
		ExamineUserBO userBO = examineTypeService.queryFlowUser(queryBO);
		// 如果找不到审批用户，根据当前审批节点找不到审批用户时的配置进行处理
		if (ObjectUtil.isNull(userBO.getRoleId()) && CollUtil.isEmpty(userBO.getUserIds())) {
			// 设置流程管理员为审批用户
			if (ObjectUtil.equal(2, fillFlow.getExamineErrorHandling())) {
				List<Long> userIds = queryExamineManagers(fillFlow.getFlowMetadataId());
				userBO.setUserIds(userIds);
			}
		}
		return userBO;
	}
}
