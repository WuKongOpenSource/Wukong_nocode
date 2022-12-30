package com.kakarote.module.service.impl;

import com.alibaba.fastjson.JSON;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.module.constant.ExamineTypeEnum;
import com.kakarote.module.entity.BO.ExamineUserBO;
import com.kakarote.module.entity.BO.ExamineUserQueryBO;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowExamineRole;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.mapper.FlowExamineRoleMapper;
import com.kakarote.module.service.IFlowExamineRoleService;
import com.kakarote.module.service.IFlowExamineTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批流程角色审批记录表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service("roleService")
public class FlowExamineRoleServiceImpl extends BaseServiceImpl<FlowExamineRoleMapper, FlowExamineRole> implements IFlowExamineRoleService, IFlowExamineTypeService {

	@Autowired
	private UserService userService;

	@Override
	public List<FlowExamineRole> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
		return lambdaQuery()
				.eq(FlowExamineRole::getModuleId, moduleId)
				.eq(FlowExamineRole::getVersion, version)
				.eq(FlowExamineRole::getFlowMetadataId, flowMetaDataId)
                .list();
	}

	@Override
	public void queryExamineData(Map<String, Object> map, String batchId) {
		List<FlowExamineRole> roles = lambdaQuery()
				.eq(FlowExamineRole::getBatchId, batchId).list();
		Map<String, FlowExamineRole> roleMap = roles.stream()
				.collect(Collectors.toMap(d -> d.getFlowId().toString(), d -> d));
		map.put(ExamineTypeEnum.ROLE.name(), roleMap);
	}

	@Override
	public FlowVO createFlowInfo(Map<String, Object> map, Flow flow, List<UserInfo> userInfos, Long ownerUserId) {
		FlowVO flowVO = new FlowVO();
		flowVO.setFlowId(flow.getFlowId());
		flowVO.setFlowName(flow.getFlowName());
		flowVO.setContent(flow.getContent());
		flowVO.setFlowType(flow.getFlowType());
		flowVO.setType(flow.getType());
		flowVO.setSort(flow.getPriority());

		Map<String, FlowExamineRole> roleMap = (Map<String, FlowExamineRole>) map.get(ExamineTypeEnum.ROLE.name());
		FlowExamineRole role = roleMap.get(flow.getFlowId().toString());
		FlowVO.FlowExamineData examineData = new FlowVO.FlowExamineData();
		examineData.setExamineType(ExamineTypeEnum.ROLE.getType());
		examineData.setRoleId(role.getRoleId());
		examineData.setType(role.getType());
		examineData.setExamineErrorHandling(flow.getExamineErrorHandling());

		List<Long> userIds = queryUserByRoleId(userInfos, role.getRoleId());
        userIds = handleUserList(userIds, flow.getFlowMetadataId());
		examineData.setUserList(searchUsers(userInfos, userIds));
		flowVO.setData(JSON.parseObject(JSON.toJSONString(examineData)));
		return flowVO;
	}

	@Override
	public ExamineUserBO queryFlowUser(ExamineUserQueryBO queryBO) {
		FlowExamineRole role = lambdaQuery().eq(FlowExamineRole::getFlowId, queryBO.getFlow().getFlowId()).one();
		ExamineUserBO userBO = new ExamineUserBO();
		userBO.setType(role.getType());
		userBO.setRoleId(role.getRoleId());
		List<UserInfo> userInfos = userService.queryUserInfoList().getData();
		List<Long> userIds = queryUserByRoleId(userInfos, role.getRoleId());
		userBO.setUserIds(userIds);
		return userBO;
	}
}
