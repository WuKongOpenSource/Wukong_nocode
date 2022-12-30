package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.module.constant.FlowMetadataTypeEnum;
import com.kakarote.module.constant.FlowStatusEnum;
import com.kakarote.module.constant.FlowTypeEnum;
import com.kakarote.module.entity.BO.MessageBO;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.mapper.FlowCopyMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 抄送节点 服务实现类
 * @author: zjj
 * @date: 2021-05-25 15:20
 */
@Service("copyService")
public class FlowCopyServiceImpl extends BaseServiceImpl<FlowCopyMapper, FlowCopy> implements IFlowCopyService, IFlowTypeService {

	@Autowired
	private IFlowService flowService;

	@Autowired
	private IFlowProvider flowProvider;

	@Autowired
	private IFlowDataDealRecordService dealRecordService;

	@Autowired
	private IModuleService moduleService;

	@Autowired
	private IModuleFieldDataService fieldDataService;

	@Autowired
	private IModuleFieldDataProvider fieldDataProvider;

	@Autowired
	private IFlowFieldAuthService fieldAuthService;

	@Autowired
	private IModuleFieldDataCommonService dataCommonService;

	@Autowired
	private IFlowExamineRecordOptionalService recordOptionalService;

	@Override
	public FlowCopy getByFlowId(Long flowId) {
		return lambdaQuery().eq(FlowCopy::getFlowId,flowId).one();
	}

	@Override
	public List<FlowCopy> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
		return lambdaQuery().eq(FlowCopy::getModuleId, moduleId)
                .eq(FlowCopy::getVersion, version)
                .eq(FlowCopy::getFlowMetadataId, flowMetaDataId)
                .list();
	}

	@Override
	public void queryExamineData(Map<String, Object> map, String batchId) {
		List<FlowCopy> flowCopies = lambdaQuery().eq(FlowCopy::getBatchId, batchId).list();
		if (CollUtil.isEmpty(flowCopies)) {
			return;
		}
		Map<String, FlowCopy> flowCopyMap = flowCopies.stream()
				.collect(Collectors.toMap(flowCopy -> flowCopy.getFlowId().toString(), flowCopy -> flowCopy));
		map.put(FlowTypeEnum.COPY.name(), flowCopyMap);
	}

	/**
	 * 一级一级向上找到level参数的位置的userId
	 *
	 * @param userInfos 所有用户list
	 * @param userId userId
	 * @param level 等级（限制递归次数）
	 * @return 按照等级从低到高排列的userId
	 */
	private List<Long> queryParentsSortByLevel(List<UserInfo> userInfos, Long userId, Integer level) {
		List<Long> userIds = new ArrayList<>();
		if (ObjectUtil.equal(0, level)) {
			return userIds;
		}
		if (ObjectUtil.isNotNull(userId) && userId > 0) {
			for (UserInfo user : userInfos) {
				if (ObjectUtil.equal(userId, user.getUserId())) {
					userIds.add(userId);
					userIds.addAll(queryParentsSortByLevel(userInfos, user.getParentId(), level - 1));
				}
			}
		}
		return userIds;
	}

	@Override
	public FlowVO createFlowInfo(Map<String, Object> map, Flow flow, List<UserInfo> userInfos, Long ownerUserId) {
		UserInfo userInfo;
		if (ObjectUtil.isNotNull(ownerUserId)) {
			userInfo = searUserInfo(userInfos, ownerUserId);
		} else {
			userInfo = searUserInfo(userInfos, UserUtil.getUserId());
		}
		FlowVO flowVO = new FlowVO();
		flowVO.setFlowId(flow.getFlowId());
		flowVO.setFlowName(flow.getFlowName());
		flowVO.setContent(flow.getContent());
		flowVO.setFlowType(flow.getFlowType());
		flowVO.setType(flow.getType());
		flowVO.setSort(flow.getPriority());
		Map<String, FlowCopy> flowCopyMap = MapUtil.get(map, FlowTypeEnum.COPY.name(), Map.class);
		FlowCopy flowCopy = flowCopyMap.get(flow.getFlowId().toString());
		FlowVO.FlowCopyData flowCopyData = new FlowVO.FlowCopyData();

		flowCopyData.setIsAdd(flowCopy.getIsAdd());
		List<Long> userIds = JSON.parseArray(flowCopy.getUserIds(), Long.class);
		List<Long> roleIds = JSON.parseArray(flowCopy.getRoleIds(), Long.class);
		List<Long> userAllIds = new LinkedList<>(userIds);
		this.addReceivers(userAllIds, flowCopy, userInfo, userInfos);
		flowCopyData.setIsSelf(flowCopy.getIsSelf());
		flowCopyData.setRoleList(roleIds);
		flowCopyData.setParentLevelList(JSON.parseArray(flowCopy.getParentLevels(), Integer.class));
		flowCopyData.setUserIdList(JSON.parseArray(flowCopy.getUserIds(), Long.class));
		flowCopyData.setUserList(searchUsers(userInfos, CollUtil.distinct(userAllIds)));
		flowVO.setData(JSON.parseObject(JSON.toJSONString(flowCopyData)));
		return flowVO;
	}

	private void addReceivers(List<Long> receivers, FlowCopy flowCopy, UserInfo userInfo, List<UserInfo> userInfos) {
		List<Long> roleIds = JSON.parseArray(flowCopy.getRoleIds(), Long.class);
		if (ObjectUtil.isNotNull(roleIds) && !roleIds.isEmpty()) {
			for (Long roleId : roleIds) {
				receivers.addAll(queryUserByRoleId(userInfos, roleId));
			}
		}
		List<Integer> parentLevels = JSON.parseArray(flowCopy.getParentLevels(), Integer.class);
		if (ObjectUtil.isNotNull(parentLevels) && !parentLevels.isEmpty()) {
			if (ObjectUtil.isNotNull(userInfo.getParentId()) && userInfo.getParentId() > 0) {
				// 将上级list升序
				List<Integer> parentLevelsAsc = parentLevels.stream().sorted().collect(Collectors.toList());
				// 此处传入当前用户的上级、配置中的最高级，返回值按照等级升序排列
				List<Long> parentUserIds = this.queryParentsSortByLevel(userInfos, userInfo.getParentId(), CollUtil.getLast(parentLevelsAsc));
				int parentMount = parentUserIds.size();
				if (parentMount > 0) {
					for (Integer level : parentLevels) {
						if (level <= parentMount) {
							// 等级作为下标，取各等级的userId
							receivers.add(CollUtil.get(parentUserIds, level -1 ));
						}
					}
				}
			}
		}
		// 是否抄送自己
		if (ObjectUtil.equal(1, flowCopy.getIsSelf())) {
			receivers.add(userInfo.getUserId());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void dealData(FlowExamineRecord record, Long flowId) {
		Flow currentFlow = flowService.getByFlowId(flowId, record.getVersion());
		FlowDataDealRecord dataDealRecord = dealRecordService.getMainByRecordIdAndFlowId(record.getRecordId(), flowId);
		// 当前节点未处理
		if (ObjectUtil.isNull(dataDealRecord)) {
			FlowCopy flowCopy = getByFlowId(currentFlow.getFlowId());
			List<UserInfo> userInfos = ApplicationContextHolder.getBean(UserService.class).queryUserInfoList().getData();
			UserInfo userInfo = searUserInfo(userInfos, record.getCreateUserId());
			List<Long> receivers = JSON.parseArray(flowCopy.getUserIds(), Long.class);
			this.addReceivers(receivers, flowCopy, userInfo, userInfos);
			List<FlowExamineRecordOptional> optionalList = recordOptionalService.getOptional(record.getRecordId(), flowId);
			List<Long> optionalUserIds = optionalList.stream().map(FlowExamineRecordOptional::getUserId).collect(Collectors.toList());
			receivers.addAll(optionalUserIds);
			receivers = CollUtil.distinct(receivers);

			// 保存节点处理记录
			dataDealRecord = new FlowDataDealRecord();
			dataDealRecord.setIsMain(true);
			dataDealRecord.setRecordId(record.getRecordId());
			dataDealRecord.setFlowId(currentFlow.getFlowId());
			dataDealRecord.setDataId(record.getDataId());
			dataDealRecord.setUserId(record.getCreateUserId());
			dataDealRecord.setCreateUserId(record.getCreateUserId());
			dataDealRecord.setCreateTime(DateUtil.date());
			dataDealRecord.setUpdateTime(DateUtil.date());
			dataDealRecord.setModuleId(record.getModuleId());
			dataDealRecord.setVersion(record.getVersion());
			dataDealRecord.setFlowType(FlowTypeEnum.COPY.getType());
			dataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
			dataDealRecord.setBatchId(IdUtil.simpleUUID());
			dealRecordService.save(dataDealRecord);

			// 创建人+模块名+主字段+发送给您，请及时查看
			ModuleEntity module = moduleService.getByModuleIdAndVersion(record.getModuleId(), record.getVersion());
			String mainFieldValue = fieldDataProvider.queryValue(record.getDataId(), module.getMainFieldId());

			// 发送消息
			MessageBO messageBO = new MessageBO();
			messageBO.setDataId(record.getDataId());
			messageBO.setValue(mainFieldValue);
			messageBO.setModuleId(module.getModuleId());
			messageBO.setModuleName(module.getName());
			messageBO.setTypeId(currentFlow.getFlowId());
			messageBO.setTypeName(currentFlow.getFlowName());
			messageBO.setExtData(new JSONObject().fluentPut("entity", currentFlow).toJSONString());
            messageBO.setType(1);
			messageBO.setReceivers(receivers);
			messageBO.setCreateUserId(record.getCreateUserId());
			IMessageService messageService = ApplicationContextHolder.getBean(IMessageService.class);
			messageService.sendMessage(messageBO);
			// 保存待办信息
			List<ModuleFieldData> fieldDataList = fieldDataService.queryFieldData(record.getDataId());
			// 获取字段授权配置
			FlowFieldAuth flowFieldAuth = fieldAuthService.getByModuleIdAndFlowId(module.getModuleId(), module.getVersion(), flowId);
			if (CollUtil.isNotEmpty(receivers)) {
				List<ToDo> toDoList = new ArrayList<>();
				for (Long receiver : receivers) {
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
					toDo.setFlowType(FlowTypeEnum.COPY.getType());
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

			// 更新数据的流程状态
			if (ObjectUtil.equal(FlowMetadataTypeEnum.SYSTEM.getType(), record.getFlowMetadataType())) {
				ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(record.getDataId());
				dataCommon.setFlowType(FlowTypeEnum.COPY.getType());
				dataCommon.setFlowStatus(FlowStatusEnum.PASS.getStatus());
				dataCommon.setType(1);
				dataCommon.setCurrentFlowId(flowId);
				dataCommon.setUpdateTime(DateUtil.date());
				dataCommonService.updateById(dataCommon);
				savePage(record.getDataId(), record.getModuleId(), record.getVersion());
			}
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
}
