package com.kakarote.module.service.impl;

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
import com.kakarote.module.entity.PO.FlowExamineContinuousSuperior;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.mapper.FlowExamineContinuousSuperiorMapper;
import com.kakarote.module.service.IFlowExamineContinuousSuperiorService;
import com.kakarote.module.service.IFlowExamineTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批流程连续多级主管审批记录表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service("continuousSuperiorService")
public class FlowExamineContinuousSuperiorServiceImpl extends BaseServiceImpl<FlowExamineContinuousSuperiorMapper,
		FlowExamineContinuousSuperior> implements IFlowExamineContinuousSuperiorService, IFlowExamineTypeService {

	@Autowired
	private UserService userService;

	@Override
	public List<FlowExamineContinuousSuperior> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
		return lambdaQuery()
				.eq(FlowExamineContinuousSuperior::getModuleId, moduleId)
				.eq(FlowExamineContinuousSuperior::getVersion, version)
				.eq(FlowExamineContinuousSuperior::getFlowMetadataId, flowMetaDataId)
                .list();
	}

	@Override
	public void queryExamineData(Map<String, Object> map, String batchId) {
		List<FlowExamineContinuousSuperior> continuousSuperiorList = lambdaQuery()
				.eq(FlowExamineContinuousSuperior::getBatchId, batchId).list();
		Map<String, FlowExamineContinuousSuperior> continuousSuperiorMap = continuousSuperiorList.stream()
				.collect(Collectors.toMap(d -> d.getFlowId().toString(), d -> d));
		map.put(ExamineTypeEnum.CONTINUOUS_SUPERIOR.name(), continuousSuperiorMap);
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
		Map<String, FlowExamineContinuousSuperior> continuousSuperiorMap =
				(Map<String, FlowExamineContinuousSuperior>) map.get(ExamineTypeEnum.CONTINUOUS_SUPERIOR.name());
		FlowExamineContinuousSuperior continuousSuperior = continuousSuperiorMap.get(flow.getFlowId().toString());
		FlowVO.FlowExamineData examineData = new FlowVO.FlowExamineData();
		examineData.setExamineType(ExamineTypeEnum.CONTINUOUS_SUPERIOR.getType());
		examineData.setType(continuousSuperior.getType());
		examineData.setRoleId(continuousSuperior.getRoleId());
		examineData.setParentLevel(continuousSuperior.getMaxLevel());
		examineData.setExamineErrorHandling(flow.getExamineErrorHandling());

		List<Long> userIds = new ArrayList<>();
		if (ObjectUtil.isNotNull(userInfo.getParentId()) && userInfo.getParentId() > 0) {
			userIds = queryUser(userInfos, continuousSuperior.getMaxLevel(), continuousSuperior.getRoleId(),
					userInfo.getParentId());
		}
        userIds = handleUserList(userIds, flow.getFlowMetadataId());
		examineData.setUserList(searchUsers(userInfos, userIds));
		flowVO.setData(JSON.parseObject(JSON.toJSONString(examineData)));
		return flowVO;
	}

	private List<Long> queryUser(List<UserInfo> userInfoList, Integer maxLevel, Long roleId, Long userId) {
		List<Long> idList = new ArrayList<>();
		if (maxLevel != null) {
			if (maxLevel < 0) {
				return idList;
			}
			maxLevel--;
		}
		for (UserInfo userInfo : userInfoList) {
			if (userId.equals(userInfo.getUserId())) {
				idList.add(userId);
				if (userInfo.getRoles().contains(roleId)) {
					return idList;
				}
				if (userInfo.getParentId() == null || userInfo.getParentId() == 0) {
					return idList;
				}
				idList.addAll(queryUser(userInfoList, maxLevel, roleId, userInfo.getParentId()));
			}
		}
		return idList;
	}

	@Override
	public ExamineUserBO queryFlowUser(ExamineUserQueryBO queryBO) {
		List<UserInfo> userInfos = userService.queryUserInfoList().getData();
		FlowExamineContinuousSuperior continuousSuperior = lambdaQuery().eq(FlowExamineContinuousSuperior::getFlowId,
				queryBO.getFlow().getFlowId()).one();
		// 当前审核人
		UserInfo userInfo = searUserInfo(userInfos, queryBO.getCreateUserId());
		if (ObjectUtil.isNull(userInfo)) {
			throw new BusinessException(SystemCodeEnum.SYSTEM_NO_VALID);
		}
		ExamineUserBO userBO = new ExamineUserBO();
		List<Long> userIds = new ArrayList<>();
		if (ObjectUtil.isNotNull(userInfo.getParentId()) && userInfo.getParentId() > 0) {
			userIds = queryUser(userInfos, continuousSuperior.getMaxLevel(), continuousSuperior.getRoleId(),
					userInfo.getParentId());
			userBO.setType(1);
		} else {
			userBO.setType(3);
		}
        userIds = handleUserList(userIds, queryBO.getFlow().getFlowMetadataId());
		userBO.setUserIds(userIds);
		return userBO;
	}
}
