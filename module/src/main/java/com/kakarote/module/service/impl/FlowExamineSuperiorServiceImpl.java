package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.module.constant.ExamineTypeEnum;
import com.kakarote.module.entity.BO.ExamineUserBO;
import com.kakarote.module.entity.BO.ExamineUserQueryBO;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowExamineSuperior;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.mapper.FlowExamineSuperiorMapper;
import com.kakarote.module.service.IFlowExamineSuperiorService;
import com.kakarote.module.service.IFlowExamineTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批流程主管审批记录表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service("superiorService")
public class FlowExamineSuperiorServiceImpl extends BaseServiceImpl<FlowExamineSuperiorMapper, FlowExamineSuperior> implements IFlowExamineSuperiorService, IFlowExamineTypeService {

	@Autowired
	private UserService userService;

	@Override
	public List<FlowExamineSuperior> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
		return lambdaQuery()
				.eq(FlowExamineSuperior::getModuleId, moduleId)
				.eq(FlowExamineSuperior::getVersion, version)
				.eq(FlowExamineSuperior::getFlowMetadataId, flowMetaDataId)
                .list();
	}

	@Override
	public void queryExamineData(Map<String, Object> map, String batchId) {
		List<FlowExamineSuperior> superiors = lambdaQuery()
				.eq(FlowExamineSuperior::getBatchId, batchId).list();
		Map<String, FlowExamineSuperior> superiorMap = superiors.stream()
				.collect(Collectors.toMap(s -> s.getFlowId().toString(), s -> s));
		map.put(ExamineTypeEnum.SUPERIOR.name(), superiorMap);
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

		Map<String, FlowExamineSuperior> superiorMap = (Map<String, FlowExamineSuperior>) map.get(ExamineTypeEnum.SUPERIOR.name());
		FlowExamineSuperior superior = superiorMap.get(flow.getFlowId().toString());
		FlowVO.FlowExamineData examineData = new FlowVO.FlowExamineData();
		examineData.setExamineType(ExamineTypeEnum.SUPERIOR.getType());
		examineData.setType(superior.getType());
		examineData.setParentLevel(superior.getParentLevel());
		examineData.setExamineErrorHandling(flow.getExamineErrorHandling());

		List<Long> userIds = new ArrayList<>();
		if (ObjectUtil.isNotNull(userInfo.getParentId()) && userInfo.getParentId() > 0) {
			List<Long> parentUserIds = queryParentUser(userInfos, userInfo.getParentId());
			if (superior.getParentLevel() > parentUserIds.size()) {
				userIds.add(CollUtil.getLast(parentUserIds));
			} else {
				userIds.add(CollUtil.get(parentUserIds, superior.getParentLevel() -1 ));
			}
 		}
        userIds = handleUserList(userIds, flow.getFlowMetadataId());
		examineData.setUserList(searchUsers(userInfos, userIds));
		flowVO.setData(JSON.parseObject(JSON.toJSONString(examineData)));
		return flowVO;
	}

	@Override
	public ExamineUserBO queryFlowUser(ExamineUserQueryBO queryBO) {
		List<UserInfo> userInfos = userService.queryUserInfoList().getData();
		FlowExamineSuperior superior = lambdaQuery().eq(FlowExamineSuperior::getFlowId,
				queryBO.getFlow().getFlowId()).one();
		// 当前审核人
		UserInfo userInfo = searUserInfo(userInfos, queryBO.getCreateUserId());
		if (ObjectUtil.isNull(userInfo)) {
			throw new BusinessException(SystemCodeEnum.SYSTEM_NO_VALID);
		}
		ExamineUserBO userBO = new ExamineUserBO();
		List<Long> userIds = new ArrayList<>();
		if (ObjectUtil.isNotNull(userInfo.getParentId()) && userInfo.getParentId() > 0) {
			List<Long> parentUserIds = queryParentUser(userInfos, userInfo.getParentId());
			if (superior.getParentLevel() > parentUserIds.size()) {
				userIds.add(CollUtil.getLast(parentUserIds));
			} else {
				userIds.add(CollUtil.get(parentUserIds, superior.getParentLevel() -1 ));
			}
		}
        userIds = handleUserList(userIds, queryBO.getFlow().getFlowMetadataId());
		userBO.setUserIds(userIds);
		userBO.setType(3);
		return userBO;
	}
}
