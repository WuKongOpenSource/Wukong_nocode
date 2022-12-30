package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.constant.ExamineTypeEnum;
import com.kakarote.module.entity.BO.ExamineUserBO;
import com.kakarote.module.entity.BO.ExamineUserQueryBO;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowExamineMember;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.mapper.FlowExamineMemberMapper;
import com.kakarote.module.service.IFlowExamineMemberService;
import com.kakarote.module.service.IFlowExamineTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批流程指定成员记录表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service("memberService")
public class FlowExamineMemberServiceImpl extends BaseServiceImpl<FlowExamineMemberMapper, FlowExamineMember> implements IFlowExamineMemberService, IFlowExamineTypeService {

	@Override
	public List<FlowExamineMember> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
		return lambdaQuery()
				.eq(FlowExamineMember::getModuleId, moduleId)
				.eq(FlowExamineMember::getVersion, version)
				.eq(FlowExamineMember::getFlowMetadataId, flowMetaDataId)
                .list();
	}

	@Override
	public void queryExamineData(Map<String, Object> map, String batchId) {
		List<FlowExamineMember> members = lambdaQuery()
				.eq(FlowExamineMember::getBatchId, batchId).list();
		Map<String, List<FlowExamineMember>> memberMap = members.stream()
				.collect(Collectors.groupingBy(d -> d.getFlowId().toString()));
		map.put(ExamineTypeEnum.MEMBER.name(), memberMap);
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

		Map<String, List<FlowExamineMember>> memberMap = (Map<String, List<FlowExamineMember>>) map.get(ExamineTypeEnum.MEMBER.name());
		List<FlowExamineMember> examineMembers = memberMap.get(flow.getFlowId().toString());
		FlowVO.FlowExamineData examineData = new FlowVO.FlowExamineData();
		examineData.setExamineType(ExamineTypeEnum.MEMBER.getType());
		examineData.setExamineErrorHandling(flow.getExamineErrorHandling());
		if (CollUtil.isNotEmpty(examineMembers)) {
			List<Long> userIds = examineMembers.stream().map(d -> d.getUserId()).collect(Collectors.toList());
			examineData.setUserList(searchUsers(userInfos, userIds));
			examineData.setType(examineMembers.get(0).getType());
		}
		flowVO.setData(JSON.parseObject(JSON.toJSONString(examineData)));
		return flowVO;
	}

	@Override
	public ExamineUserBO queryFlowUser(ExamineUserQueryBO queryBO) {
		List<FlowExamineMember> members = lambdaQuery().eq(FlowExamineMember::getFlowId, queryBO.getFlow().getFlowId())
				.orderByAsc(FlowExamineMember::getSort).list();
		ExamineUserBO userBO = new ExamineUserBO();
		if (CollUtil.isEmpty(members)) {
			return userBO;
		}
		userBO.setType(members.get(0).getType());
		List<Long> userIds = members.stream().map(FlowExamineMember::getUserId).collect(Collectors.toList());
		userBO.setUserIds(userIds);
		return userBO;
	}
}
