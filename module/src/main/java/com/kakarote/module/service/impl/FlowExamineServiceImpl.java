package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.common.TimeValueUtil;
import com.kakarote.module.common.mq.ProducerUtil;
import com.kakarote.module.constant.*;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.mapper.ModuleFieldDataMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @description: 审批节点 服务类
 * @author: zjj
 * @date: 2021-05-25 10:40
 */
@Service("examineService")
public class FlowExamineServiceImpl implements IFlowTypeService {

	@Autowired
	private IFlowTimeLimitService timeLimitService;

	@Autowired
	private IFlowService flowService;

	@Autowired
	private IFlowProvider flowProvider;

	@Autowired
	private IModuleService moduleService;

	@Autowired
	private IModuleFieldDataProvider fieldDataProvider;

	@Autowired
	private ModuleFieldDataMapper fieldDataMapper;

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
		return flowVO;
	}

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
				// 如果当前审批失效
				if (ObjectUtil.equal(1, dealRecord.getInvalidType())) {
					dealRecord.setFlowStatus(FlowStatusEnum.INVALID.getStatus());
					dealRecord.setUpdateTime(DateUtil.date());
					dealRecordService.updateById(dealRecord);
					return;
				} else {
					dealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
				}
				// 是转交的审批
				if (ObjectUtil.isNotNull(dealRecord.getParentId())) {
					// 1 依次审批 2 会签 3 或签
					if (ObjectUtil.equal(1, dealRecord.getType())) {
						dealRecord.setTimeValue(timeLimit.getTimeValue());
						dealRecord.setOvertimeType(timeLimit.getOvertimeType());
						dealRecord.setUpdateTime(DateUtil.date());
						dealRecordService.updateById(dealRecord);
					} else {
						dealRecordService.lambdaUpdate()
								.set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.PASS.getStatus())
								.set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
								.set(FlowDataDealRecord::getTimeValue, timeLimit.getTimeValue())
								.set(FlowDataDealRecord::getOvertimeType, timeLimit.getOvertimeType())
								.ne(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.PASS.getStatus())
								.eq(FlowDataDealRecord::getParentId, dealRecord.getParentId())
								.eq(FlowDataDealRecord::getBatchId, mainDataDealRecord.getBatchId())
								.update();
					}
				} else {
					dealRecord.setTimeValue(timeLimit.getTimeValue());
					dealRecord.setOvertimeType(timeLimit.getOvertimeType());
					dealRecord.setUpdateTime(DateUtil.date());
					dealRecordService.updateById(dealRecord);
				}
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
					msgBody.setMsgTag(MessageTagEnum.EXAMINE_TIME_LIMIT);
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
						.set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.PASS.getStatus())
						.set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
						.set(FlowDataDealRecord::getTimeValue, timeLimit.getTimeValue())
						.set(FlowDataDealRecord::getOvertimeType, timeLimit.getOvertimeType())
						.ne(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.PASS.getStatus())
						.eq(FlowDataDealRecord::getBatchId, mainDataDealRecord.getBatchId())
						.update();
			}

			mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
			mainDataDealRecord.setUpdateTime(DateUtil.date());
			dealRecordService.updateById(mainDataDealRecord);
            // 更新数据的流程状态
            ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(mainDataDealRecord.getDataId());
            dataCommon.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
            dataCommon.setType(0);
            dataCommon.setCurrentFlowId(mainDataDealRecord.getFlowId());
			dataCommon.setUpdateTime(DateUtil.date());
			dataCommonService.updateById(dataCommon);
            savePage(mainDataDealRecord.getDataId(), mainDataDealRecord.getModuleId(), mainDataDealRecord.getVersion());
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
        List<Long> userIds = JSON.parseArray(timeLimit.getUserIds(), Long.class);
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
			messageBO.setReceivers(userIds);
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
			// 变更负责人，原有的负责人记录置为失效
			dealRecordService.lambdaUpdate()
					.set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.INVALID.getStatus())
					.set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
					.set(FlowDataDealRecord::getTimeValue, timeLimit.getTimeValue())
					.set(FlowDataDealRecord::getOvertimeType, timeLimit.getOvertimeType())
					.eq(FlowDataDealRecord::getRecordId, mainDataDealRecord.getRecordId())
					.eq(FlowDataDealRecord::getIsMain, false)
					.eq(FlowDataDealRecord::getFlowId,mainDataDealRecord.getFlowId()).update();
			// 生成审核日志
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
				flowDataDealRecord.setFlowId(mainDataDealRecord.getFlowId());
				flowDataDealRecord.setDataId(mainDataDealRecord.getDataId());
				flowDataDealRecord.setType(timeLimit.getExamineType());
				flowDataDealRecord.setOvertimeType(timeLimit.getOvertimeType());
				flowDataDealRecord.setTimeValue(timeLimit.getTimeValue());
				flowDataDealRecord.setCreateTime(currentDate);
				flowDataDealRecord.setModuleId(mainDataDealRecord.getModuleId());
				flowDataDealRecord.setVersion(mainDataDealRecord.getVersion());
				flowDataDealRecord.setFlowType(FlowTypeEnum.EXAMINE.getType());
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
			messageBO.setStatus(FlowStatusEnum.DEALING.getStatus());
			messageBO.setReceivers(receivers);
			messageBO.setCreateUserId(mainDataDealRecord.getCreateUserId());
			IMessageService messageService = ApplicationContextHolder.getBean(IMessageService.class);
			messageService.sendMessage(messageBO);
		}
	}

	@Override
	public void dealData(FlowExamineRecord record, Long flowId) {
		Flow currentFlow = flowService.getByFlowId(flowId, record.getVersion());
		FlowDataDealRecord dataDealRecord = dealRecordService.getMainByRecordIdAndFlowId(record.getRecordId(), flowId);
		// 当前节点未处理，直接结束不执行下一个节点
		if (ObjectUtil.isNull(dataDealRecord)) {
			// 保存主记录
			dataDealRecord = new FlowDataDealRecord();

			ModuleEntity module = moduleService.getByModuleIdAndVersion(record.getModuleId(), record.getVersion());
			String batchId = IdUtil.simpleUUID();

			// 节点负责人ID
			ExamineUserBO userBO = queryExamineUser(currentFlow, record.getCreateUserId(), record.getRecordId());
			List<FlowDataDealRecord> records = dealRecordService.saveRecords(currentFlow, userBO, record.getRecordId(), record.getCreateUserId(), record.getDataId(), batchId);
			List<Long> userIds = records.stream()
					.filter(r -> ObjectUtil.equal(FlowStatusEnum.DEALING.getStatus(), r.getFlowStatus()))
					.map(FlowDataDealRecord::getUserId).collect(Collectors.toList());
			// 保存消息待办
			if (CollUtil.isNotEmpty(userIds)) {
				List<ToDo> toDoList = new ArrayList<>();
				List<ModuleFieldData> fieldDataList = fieldDataMapper.getByDataId(record.getDataId());
				// 获取字段授权配置
				FlowFieldAuth flowFieldAuth = fieldAuthService.getByModuleIdAndFlowId(module.getModuleId(), module.getVersion(), flowId);
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
					msgBody.setMsgTag(MessageTagEnum.EXAMINE_TIME_LIMIT);
					msgBody.setMsgKey(IdUtil.simpleUUID());
					msgBody.setModuleId(module.getModuleId());
					msgBody.setVersion(module.getVersion());
					msgBody.setDataId(record.getDataId());
					msgBody.setUserId(UserUtil.getUserId());
					msgBody.setFlowId(flowId);
					msgBody.setRecordId(record.getRecordId());
					msgBody.setDelayTime(delayTime);
					producerUtil.sendMsgToTopicOne(msgBody);
					// 审批限时处理-前端显示所需字段值
					dataDealRecord.setOvertimeType(timeLimit.getOvertimeType());
					dataDealRecord.setTimeValue(timeLimit.getTimeValue());
				}
			}

			dataDealRecord.setIsMain(true);
			dataDealRecord.setRecordId(record.getRecordId());
			dataDealRecord.setFlowId(currentFlow.getFlowId());
			dataDealRecord.setDataId(record.getDataId());
			dataDealRecord.setUserId(record.getCreateUserId());
			dataDealRecord.setCreateUserId(record.getCreateUserId());
			dataDealRecord.setCreateTime(DateUtil.date());
			dataDealRecord.setModuleId(record.getModuleId());
			dataDealRecord.setVersion(record.getVersion());
			dataDealRecord.setType(currentFlow.getType());
			dataDealRecord.setFlowType(FlowTypeEnum.EXAMINE.getType());
			dataDealRecord.setFlowStatus(FlowStatusEnum.WAIT.getStatus());
			dataDealRecord.setBatchId(batchId);
			dealRecordService.save(dataDealRecord);

			// 更新数据的流程状态
			ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(record.getDataId());
			dataCommon.setFlowStatus(FlowStatusEnum.WAIT.getStatus());
			dataCommon.setFlowType(FlowTypeEnum.EXAMINE.getType());
			dataCommon.setType(0);
			dataCommon.setCurrentFlowId(flowId);
			dataCommon.setUpdateTime(DateUtil.date());
			dataCommonService.updateById(dataCommon);
			savePage(record.getDataId(), record.getModuleId(), record.getVersion());
			return;
		}
		// 没有审核通过，不能进行下一个节点
		else if (ObjectUtil.notEqual(FlowStatusEnum.PASS.getStatus(), dataDealRecord.getFlowStatus())) {
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

	private ExamineUserBO queryExamineUser(Flow examineFlow, Long createUserId, Long recordId) {
		ExamineTypeEnum examineTypeEnum = ExamineTypeEnum.parse(examineFlow.getType());
		IFlowExamineTypeService examineTypeService = ApplicationContextHolder.getBean(examineTypeEnum.getServiceName());
		ExamineUserQueryBO queryBO = new ExamineUserQueryBO();
		queryBO.setFlow(examineFlow);
		queryBO.setCreateUserId(createUserId);
		queryBO.setRecordId(recordId);
		ExamineUserBO userBO = examineTypeService.queryFlowUser(queryBO);
		// 如果找不到审批用户，根据当前审批节点找不到审批用户时的配置进行处理
		if (ObjectUtil.isNull(userBO.getRoleId()) && CollUtil.isEmpty(userBO.getUserIds())) {
			// 设置流程管理员为审批用户
			if (ObjectUtil.equal(2, examineFlow.getExamineErrorHandling())) {
				List<Long> userIds = queryExamineManagers(examineFlow.getFlowMetadataId());
				userBO.setUserIds(userIds);
			}
		}
		return userBO;
	}
}
