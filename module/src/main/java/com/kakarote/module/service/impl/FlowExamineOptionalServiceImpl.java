package com.kakarote.module.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.constant.ExamineTypeEnum;
import com.kakarote.module.entity.BO.ExamineUserBO;
import com.kakarote.module.entity.BO.ExamineUserQueryBO;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowExamineOptional;
import com.kakarote.module.entity.PO.FlowExamineRecordOptional;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.mapper.FlowExamineOptionalMapper;
import com.kakarote.module.service.IFlowExamineOptionalService;
import com.kakarote.module.service.IFlowExamineRecordOptionalService;
import com.kakarote.module.service.IFlowExamineTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批流程自选成员记录表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service("optionalService")
public class FlowExamineOptionalServiceImpl extends BaseServiceImpl<FlowExamineOptionalMapper, FlowExamineOptional>
		implements IFlowExamineOptionalService, IFlowExamineTypeService {

	private static final int THREE =3;

	private static final int TWO =2;

	@Autowired
	private IFlowExamineRecordOptionalService recordOptionalService;

	@Override
	public List<FlowExamineOptional> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
		return lambdaQuery()
				.eq(FlowExamineOptional::getModuleId, moduleId)
				.eq(FlowExamineOptional::getVersion, version)
				.eq(FlowExamineOptional::getFlowMetadataId, flowMetaDataId)
                .list();
	}

	@Override
	public void queryExamineData(Map<String, Object> map, String batchId) {
		List<FlowExamineOptional> optionals = lambdaQuery()
				.eq(FlowExamineOptional::getBatchId, batchId).list();
		Map<String, List<FlowExamineOptional>> roleMap = optionals.stream()
				.collect(Collectors.groupingBy(d -> d.getFlowId().toString()));
		map.put(ExamineTypeEnum.OPTIONAL.name(), roleMap);
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

		Map<String, List<FlowExamineOptional>> optionalMap = (Map<String, List<FlowExamineOptional>>) map.get(ExamineTypeEnum.OPTIONAL.name());
		List<FlowExamineOptional> optionals = optionalMap.get(flow.getFlowId().toString());
		optionals.sort((o1, o2) -> o1.getSort() > o2.getSort() ? 1 : -1);
		Integer rangeType = optionals.get(0).getRangeType();
		FlowVO.FlowExamineData examineData = new FlowVO.FlowExamineData();
		examineData.setExamineType(ExamineTypeEnum.OPTIONAL.getType());
		examineData.setType(optionals.get(0).getType());
		examineData.setChooseType(optionals.get(0).getChooseType());
		examineData.setRangeType(rangeType);
		examineData.setExamineErrorHandling(flow.getExamineErrorHandling());

		List<Long> userIds = new ArrayList<>();
		// 指定角色
		if (ObjectUtil.equal(THREE, rangeType)) {
			Long roleId = optionals.get(0).getRoleId();
			userIds = queryUserByRoleId(userInfos, roleId);
			examineData.setRoleId(roleId);
		}
		// 指定人员
		else if (ObjectUtil.equal(TWO, rangeType)) {
			userIds = optionals.stream().map(d -> d.getUserId()).collect(Collectors.toList());
		}
		examineData.setUserList(searchUsers(userInfos, userIds));
		flowVO.setData(JSON.parseObject(JSON.toJSONString(examineData)));
		return flowVO;
	}

	@Override
	public ExamineUserBO queryFlowUser(ExamineUserQueryBO queryBO) {
		List<FlowExamineOptional> optionals = lambdaQuery().eq(FlowExamineOptional::getFlowId, queryBO.getFlow().getFlowId())
				.orderByAsc(FlowExamineOptional::getSort).list();
		ExamineUserBO userBO = new ExamineUserBO();
		userBO.setType(optionals.get(0).getType());
		if (ObjectUtil.isNotNull(queryBO.getRecordId())) {
			List<FlowExamineRecordOptional> recordOptionals = recordOptionalService.lambdaQuery()
					.select(FlowExamineRecordOptional::getUserId)
					.eq(FlowExamineRecordOptional::getFlowId, queryBO.getFlow().getFlowId())
					.eq(FlowExamineRecordOptional::getRecordId, queryBO.getRecordId())
					.orderByAsc(FlowExamineRecordOptional::getSort).list();
			List<Long> userIds = recordOptionals.stream().map(FlowExamineRecordOptional::getUserId).collect(Collectors.toList());
			userBO.setUserIds(userIds);
		}
		return userBO;
	}
}
