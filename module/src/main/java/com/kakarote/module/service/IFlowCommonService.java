package com.kakarote.module.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.constant.Const;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.common.EasyExcelParseUtil;
import com.kakarote.module.constant.FieldSearchEnum;
import com.kakarote.module.constant.FlowTypeEnum;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.PO.ModuleRoleUser;
import com.kakarote.module.entity.VO.FlowVO;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-28 13:53
 */
public interface IFlowCommonService extends ModulePageService{

	default FlowVO.User searchUser(List<UserInfo> userInfos, Long userId) {
		UserInfo userInfo = this.searUserInfo(userInfos, userId);
		if (ObjectUtil.isNotNull(userInfo)) {
			FlowVO.User user = new FlowVO.User();
			user.setUserId(userId);
			user.setRealname(userInfo.getNickname());
			user.setImg(userInfo.getUserImg());
			return user;
		}
		return null;
	}

	default List<FlowVO.User> searchUsers(List<UserInfo> userInfos, List<Long> userIds) {
		List<Long> ids = CollUtil.distinct(userIds);
		return userInfos.stream().filter(u -> ids.contains(u.getUserId())).map(u -> {
			FlowVO.User user = new FlowVO.User();
			user.setUserId(u.getUserId());
			user.setRealname(u.getNickname());
			user.setImg(u.getUserImg());
			return user;
		}).collect(Collectors.toList());
	}

	default UserInfo searUserInfo(List<UserInfo> userInfos, Long userId) {
		return userInfos.stream().filter(u -> ObjectUtil.equal(userId, u.getUserId()))
				.findFirst().orElse(null);
	}

	/**
	 * 获取指定角色的用户
	 *
	 * @param userInfos
	 * @param roleId
	 * @return
	 */
	default List<Long> queryUserByRoleId(List<UserInfo> userInfos, Long roleId) {
		List<Long> userIdList = userInfos.stream().filter(u -> u.getRoles().contains(roleId))
				.map(UserInfo::getUserId).distinct().collect(Collectors.toList());
		// 无代码用户角色关系
		List<ModuleRoleUser> moduleRoleUsers = ApplicationContextHolder.getBean(IModuleRoleUserService.class).getByRoleId(roleId);
		List<Long> userIds = moduleRoleUsers.stream()
				.filter(m -> !CollUtil.contains(userIdList, m.getUserId()))
				.map(ModuleRoleUser::getUserId).collect(Collectors.toList());
		userIdList.addAll(userIds);
		return userIdList;
	}

	/**
	 * 获取指定用户的角色
	 *
	 * @param userId
	 * @return
	 */
	default List<Long> queryRoleByUserId(Long userId) {
		List<Long> roleIdList = new ArrayList<>();
		UserInfo user = UserCacheUtil.getUserInfo(userId);
		if (ObjectUtil.isNotNull(user)) {
			roleIdList = user.getRoles().stream().collect(Collectors.toList());
			// 无代码用户角色关系
			List<ModuleRoleUser> moduleRoleUsers = ApplicationContextHolder.getBean(IModuleRoleUserService.class).getByUserId(userId);
			List<Long> roleIds = moduleRoleUsers.stream()
					.map(ModuleRoleUser::getRoleId).collect(Collectors.toList());
			roleIdList.addAll(roleIds);
		}

		return roleIdList;
	}

	/**
	 * 获取当前用户的所有上级用户（包含当前用户）
	 *
	 * @param userInfos
	 * @param userId
	 * @return
	 */
	default List<Long> queryParentUser(List<UserInfo> userInfos, Long userId) {
		List<Long> userIds = new ArrayList<>();
		if (ObjectUtil.isNotNull(userId) && userId > 0) {
			UserInfo userInfo = userInfos.stream().filter(u -> ObjectUtil.equal(userId, u.getUserId())).findFirst().orElse(null);
			if (ObjectUtil.isNotNull(userInfo)) {
				userIds.add(userId);
				userIds.addAll(queryParentUser(userInfos, userInfo.getParentId()));
			}
		}
		return CollUtil.distinct(userIds);
	}

	/**
	 * 如果找不到审核人，返回流程管理员
	 *
	 * @param userIds
	 * @param metaDataId
	 * @return
	 */
	default List<Long> handleUserList(List<Long> userIds, Long metaDataId){
		if (CollUtil.isNotEmpty(userIds)) {
			UserService adminService = ApplicationContextHolder.getBean(UserService.class);
			userIds = adminService.queryNormalUserByIds(userIds).getData();
		}
		if (CollUtil.isEmpty(userIds)) {
			userIds = queryExamineManagers(metaDataId);
		}
		return CollUtil.distinct(userIds);
	}

	/**
	 * 查询流程管理员
	 *
	 * @param metaDataId
	 * @return
	 */
	default List<Long> queryExamineManagers(Long metaDataId) {
		IFlowMetadataService metadataService = ApplicationContextHolder.getBean(IFlowMetadataService.class);
		List<Long> managerUserIds = metadataService.getManagerUserIds(metaDataId);
		UserService userService = ApplicationContextHolder.getBean(UserService.class);
		managerUserIds = userService.queryNormalUserByIds(managerUserIds).getData();
		if (CollUtil.isEmpty(managerUserIds)) {
			managerUserIds.add(userService.querySuperUserId().getData());
		}
		return CollUtil.distinct(managerUserIds);
	}

	/**
	 * 条件节点筛选条件是否通过
	 *
	 * @param conditionBOS  筛选条件
	 * @param fieldValueBOS 字段数据值
	 * @param moduleId      模块ID
	 * @param version       版本号
	 * @return
	 */
	default Boolean conditionPass(List<FlowConditionBO> conditionBOS, List<ModuleFieldValueBO> fieldValueBOS, Long moduleId, Integer version) {
		Map<Integer, List<CommonConditionBO>> conditionMap = conditionBOS.stream()
				.collect(Collectors.groupingBy(FlowConditionBO::getGroupId, Collectors.mapping(FlowConditionBO::getSearch, Collectors.toList())));
		for (List<CommonConditionBO> conditionBOList : conditionMap.values()) {
			boolean isPass = this.commonConditionPass(conditionBOList, fieldValueBOS, moduleId, version);
			if (!isPass) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 通用条件判断是否通过
	 *
	 * @param conditionBOList
	 * @param fieldValueBOS
	 * @param moduleId
	 * @param version
	 * @return
	 */
	default Boolean commonConditionPass(List<CommonConditionBO> conditionBOList, List<ModuleFieldValueBO> fieldValueBOS, Long moduleId, Integer version) {
		boolean isPass = true;
		if (CollUtil.isEmpty(conditionBOList)) {
			return true;
		}
		Long createUserId = fieldValueBOS.stream()
				.filter(f -> ObjectUtil.equal("createUserName", f.getFieldName()) && ObjectUtil.isNotNull(f.getValue()))
				.findFirst().map(ModuleFieldValueBO::getValue).map(Long::parseLong).orElse(null);
		if (ObjectUtil.isNull(createUserId) && ObjectUtil.isNotNull(UserUtil.getUser())){
			createUserId = UserUtil.getUserId();
		}
		for (CommonConditionBO conditionBO : conditionBOList) {
			FieldSearchEnum conditionType = conditionBO.getConditionType();
			// 字段类型
			String formType = conditionBO.getFormType();
			ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(formType);
			String fieldName = conditionBO.getFieldName();
			// 预设值
			List<String> valuesPre = conditionBO.getValues();
			valuesPre = parseValueByType(fieldEnum, valuesPre);
			// 发起人 创建人
			if (StrUtil.equals("data_user", fieldName) || StrUtil.equals("createUserName", fieldName)) {
				if (CollUtil.contains(conditionBO.getValues(), createUserId.toString())) {
					isPass = true;
					continue;
				} else {
					return false;
				}
			}
			// 部门
			if (StrUtil.equals("data_dept", fieldName)) {
				UserInfo user = UserCacheUtil.getUserInfo(createUserId);
				if (ObjectUtil.isNull(user)) {
					return false;
				}
				String deptId = user.getDeptId().toString();
				if (conditionBO.getValues().contains(deptId)) {
					isPass = true;
					continue;
				} else {
					return false;
				}
			}
			// 角色
			if (StrUtil.equals("data_role", fieldName)) {
				List<Long> roleIds = queryRoleByUserId(createUserId);
				if (CollUtil.isEmpty(roleIds)) {
					return false;
				}
				List<String> roleIdList = roleIds.stream().map(String::valueOf).collect(Collectors.toList());
				if (CollUtil.containsAny(conditionBO.getValues(), roleIdList)) {
					isPass = true;
					continue;
				} else {
					return false;
				}
			}

			ModuleFieldValueBO fieldValueBO = fieldValueBOS.stream()
					.filter(v -> v.getFieldName().equals(conditionBO.getFieldName()))
					.findFirst().orElse(null);
			if (ObjectUtil.isNull(fieldValueBO)) {
				return false;
			}
			// 公式字段，需要特殊处理
			if (ObjectUtil.equal(ModuleFieldEnum.FORMULA, fieldEnum)) {
				ModuleFieldFormulaBO formulaBO = ApplicationContextHolder.getBean(IModuleFieldFormulaService.class).queryFormulaList(moduleId, fieldValueBO.getFieldId(), version);
				// 1 数字 2 金额 3 百分比 4 日期 5 日期时间 6 文本 7 布尔值
				if (Arrays.asList(1, 2, 3).contains(formulaBO.getType())) {
					fieldEnum = ModuleFieldEnum.NUMBER;
				}
				if (Arrays.asList(4,5).contains(formulaBO.getType())) {
					fieldEnum = ModuleFieldEnum.DATE;
				}
				if (ObjectUtil.equal(6, formulaBO.getType())) {
					//TODO:暂不处理
				}
				if (ObjectUtil.equal(7, formulaBO.getType())) {
					//TODO:暂不处理
				}
			}

			// 用户输入的值
			String fieldValueFill = fieldValueBO.getValue();
			List<String> fieldValueFillList = parseValueByType(fieldEnum, fieldValueBO.getValue());

			switch (conditionType) {
				case IS: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
						break;
					}
					// 地址
					if (ObjectUtil.equal(ModuleFieldEnum.AREA_POSITION, fieldEnum)) {
						if (!CollUtil.containsAll(fieldValueFillList, valuesPre)) {
							isPass = false;
						} else {
							isPass = true;
						}
						break;
					}
					// 日期区间
					if (ObjectUtil.equal(ModuleFieldEnum.DATE_INTERVAL, fieldEnum)) {
						if (CollUtil.isEmpty(valuesPre) || CollUtil.isEmpty(fieldValueFillList)) {
							isPass = false;
						}
						// 预设值
						CommonESNestedBO dateIntervalPre = JSON.parseObject(CollUtil.getFirst(valuesPre), CommonESNestedBO.class);
						// 用户填写值
						CommonESNestedBO dateIntervalFill = JSON.parseObject(CollUtil.getFirst(fieldValueFillList), CommonESNestedBO.class);
						if (StrUtil.equals(dateIntervalPre.getFromDate(), dateIntervalFill.getFromDate())
								&& StrUtil.equals(dateIntervalPre.getToDate(), dateIntervalFill.getToDate()) ) {
							isPass = true;
						} else {
							isPass = false;
						}
						break;
					}
					if (!StrUtil.equals(CollUtil.join(valuesPre, ""), CollUtil.join(fieldValueFillList, ""))) {
						isPass = false;
					}
					break;
				}
				case IS_NOT: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
						break;
					}
					// 地址
					if (ObjectUtil.equal(ModuleFieldEnum.AREA_POSITION, fieldEnum)) {
						if (CollUtil.containsAll(fieldValueFillList, valuesPre)) {
							isPass = false;
						} else {
							isPass = true;
						}
						break;
					}
					// 日期区间
					if (ObjectUtil.equal(ModuleFieldEnum.DATE_INTERVAL, fieldEnum)) {
						if (CollUtil.isEmpty(valuesPre) || CollUtil.isEmpty(fieldValueFillList)) {
							isPass = false;
						}
						// 预设值
						CommonESNestedBO dateIntervalPre = JSON.parseObject(CollUtil.getFirst(valuesPre), CommonESNestedBO.class);
						// 用户填写值
						CommonESNestedBO dateIntervalFill = JSON.parseObject(CollUtil.getFirst(fieldValueFillList), CommonESNestedBO.class);
						if (StrUtil.equals(dateIntervalPre.getFromDate(), dateIntervalFill.getFromDate())
								&& StrUtil.equals(dateIntervalPre.getToDate(), dateIntervalFill.getToDate()) ) {
							isPass = false;
						} else {
							isPass = true;
						}
						break;
					}
					if (StrUtil.equals(CollUtil.join(valuesPre, ""), CollUtil.join(fieldValueFillList, ""))) {
						isPass = false;
					}
					break;
				}
				case CONTAINS: {
					isPass = false;
					if (CollUtil.isEmpty(fieldValueFillList)) {
						return false;
					}
					// 地址
					if (ObjectUtil.equal(ModuleFieldEnum.AREA_POSITION, fieldEnum)) {
						if (CollUtil.containsAll(fieldValueFillList, valuesPre)) {
							isPass = true;
						}
						break;
					}
					//多选
					if (Arrays.asList(ModuleFieldEnum.CHECKBOX, ModuleFieldEnum.TAG).contains(fieldEnum)) {
						if (CollUtil.containsAny(valuesPre, fieldValueFillList)) {
							isPass = true;
							break;
						}
					}
					// 日期区间
					if (ObjectUtil.equal(ModuleFieldEnum.DATE_INTERVAL, fieldEnum)) {
						if (CollUtil.isEmpty(valuesPre) || CollUtil.isEmpty(fieldValueFillList)) {
							isPass = false;
						}
						// 预设值
						CommonESNestedBO dateIntervalPre = JSON.parseObject(CollUtil.getFirst(valuesPre), CommonESNestedBO.class);
						// 用户填写值
						CommonESNestedBO dateIntervalFill = JSON.parseObject(CollUtil.getFirst(fieldValueFillList), CommonESNestedBO.class);
						if (DateUtil.parse(dateIntervalFill.getFromDate()).isBeforeOrEquals(DateUtil.parse(dateIntervalPre.getFromDate())) &&
								DateUtil.parse(dateIntervalFill.getToDate()).isAfterOrEquals(DateUtil.parse(dateIntervalPre.getFromDate()))) {
							isPass = true;
						} else {
							isPass = false;
						}
						break;
					}
					for (String value : valuesPre) {
						if (fieldValueFill.contains(value)) {
							isPass = true;
							break;
						}
					}
					break;
				}
				case NOT_CONTAINS: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
						break;
					}
					// 地址
					if (ObjectUtil.equal(ModuleFieldEnum.AREA_POSITION, fieldEnum)) {
						if (CollUtil.containsAll(fieldValueFillList, valuesPre)) {
							isPass = false;
						}
						break;
					}
					// 多选
					if (Arrays.asList(ModuleFieldEnum.CHECKBOX, ModuleFieldEnum.TAG).contains(fieldEnum)) {
						if (CollUtil.containsAny(valuesPre, fieldValueFillList)) {
							isPass = false;
							break;
						}
					}
					// 日期区间
					if (ObjectUtil.equal(ModuleFieldEnum.DATE_INTERVAL, fieldEnum)) {
						if (CollUtil.isEmpty(valuesPre) || CollUtil.isEmpty(fieldValueFillList)) {
							isPass = false;
						}
						// 预设值
						CommonESNestedBO dateIntervalPre = JSON.parseObject(CollUtil.getFirst(valuesPre), CommonESNestedBO.class);
						// 用户填写值
						CommonESNestedBO dateIntervalFill = JSON.parseObject(CollUtil.getFirst(fieldValueFillList), CommonESNestedBO.class);
						if (DateUtil.parse(dateIntervalFill.getFromDate()).isAfter(DateUtil.parse(dateIntervalPre.getFromDate())) ||
								DateUtil.parse(dateIntervalFill.getToDate()).isBefore(DateUtil.parse(dateIntervalPre.getFromDate()))) {
							isPass = true;
						} else {
							isPass = false;
						}
						break;
					}
					for (String value : valuesPre) {
						if (fieldValueFill.contains(value)) {
							isPass = false;
							break;
						}
					}
					break;
				}
				case IS_NULL: {
					if (CollUtil.isNotEmpty(fieldValueFillList)) {
						isPass = false;
					}
					break;
				}
				case IS_NOT_NULL: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
					}
					break;
				}
				case GT: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
						break;
					}
					if (Arrays.asList(ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT).contains(fieldEnum)) {
						BigDecimal value = new BigDecimal(valuesPre.get(0));
						BigDecimal realValue = new BigDecimal(fieldValueFill);
						if (value.compareTo(realValue) >= 0) {
							isPass = false;
						}
					}
					if (Arrays.asList(ModuleFieldEnum.DATE, ModuleFieldEnum.DATETIME).contains(fieldEnum)) {
						DateTime dateTime = DateTime.parse(valuesPre.get(0));
						DateTime realDateTime = DateTime.parse(fieldValueFill);
						if (dateTime.isAfter(realDateTime) || StrUtil.equals(valuesPre.get(0), fieldValueFill)) {
							isPass = false;
						}
					}
					break;
				}
				case EGT: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
						break;
					}
					if (Arrays.asList(ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT).contains(fieldEnum)) {
						BigDecimal value = new BigDecimal(valuesPre.get(0));
						BigDecimal realValue = new BigDecimal(fieldValueFill);
						if (value.compareTo(realValue) > 0) {
							isPass = false;
						}
					}
					if (Arrays.asList(ModuleFieldEnum.DATE, ModuleFieldEnum.DATETIME).contains(fieldEnum)) {
						DateTime dateTime = DateTime.parse(valuesPre.get(0));
						DateTime realDateTime = DateTime.parse(fieldValueFill);
						if (dateTime.isAfter(realDateTime)) {
							isPass = false;
						}
					}
					break;
				}
				case LT: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
						break;
					}
					if (Arrays.asList(ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT).contains(fieldEnum)) {
						BigDecimal value = new BigDecimal(valuesPre.get(0));
						BigDecimal realValue = new BigDecimal(fieldValueFill);
						if (realValue.compareTo(value) >= 0) {
							isPass = false;
						}
					}
					if (Arrays.asList(ModuleFieldEnum.DATE, ModuleFieldEnum.DATETIME).contains(fieldEnum)) {
						DateTime dateTime = DateTime.parse(valuesPre.get(0));
						DateTime realDateTime = DateTime.parse(fieldValueFill);
						if (dateTime.isBefore(realDateTime) || StrUtil.equals(valuesPre.get(0), fieldValueFill)) {
							isPass = false;
						}
					}
					break;
				}
				case ELT: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
						break;
					}
					if (Arrays.asList(ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT).contains(fieldEnum)) {
						BigDecimal value = new BigDecimal(valuesPre.get(0));
						BigDecimal realValue = new BigDecimal(fieldValueFill);
						if (realValue.compareTo(value) > 0) {
							isPass = false;
						}
					}
					if (Arrays.asList(ModuleFieldEnum.DATE, ModuleFieldEnum.DATETIME).contains(fieldEnum)) {
						DateTime dateTime = DateTime.parse(valuesPre.get(0));
						DateTime realDateTime = DateTime.parse(fieldValueFill);
						if (dateTime.isBefore(realDateTime)) {
							isPass = false;
						}
					}
					break;
				}
				case PREFIX: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
						break;
					}
					String finalFieldValue = fieldValueFill;
					String v = valuesPre.stream().filter(i -> finalFieldValue.startsWith(i)).findAny().orElse(null);
					if (ObjectUtil.isNull(v)) {
						isPass = false;
					}
					break;
				}
				case SUFFIX: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
						break;
					}
					String finalFieldValue = fieldValueFill;
					String v = valuesPre.stream().filter(i -> finalFieldValue.endsWith(i)).findAny().orElse(null);
					if (ObjectUtil.isNull(v)) {
						isPass = false;
					}
					break;
				}
				case RANGE: {
					if (CollUtil.isEmpty(fieldValueFillList)) {
						isPass = false;
						break;
					}
					if (Arrays.asList(ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT).contains(fieldEnum)) {
						BigDecimal start = new BigDecimal(valuesPre.get(0));
						BigDecimal end = new BigDecimal(valuesPre.get(1));
						if (start.compareTo(end) > 0) {
							BigDecimal tmp = BigDecimal.ZERO;
							tmp = start;
							start = end;
							end = tmp;
						}
						BigDecimal bigDecimal = new BigDecimal(fieldValueFill);
						if (bigDecimal.compareTo(start) >= 0 && bigDecimal.compareTo(end) <= 0) {
							isPass = true;
						} else {
							isPass = false;
						}
					}
					break;
				}
				default:
					break;
			}
			if (!isPass) {
				return false;
			}
		}
		return true;
	}

	default <T> List<String> parseValueByType(ModuleFieldEnum fieldEnum, T value) {
		List<String> result = new ArrayList<>();
		if (ObjectUtil.isEmpty(value)) {
			return result;
		}
		Boolean isList = value instanceof List;
		switch (fieldEnum) {
			case SELECT: {
				if (isList) {
					List<ModuleOptionsBO> v = (List<ModuleOptionsBO>) ((ArrayList) value).stream().map(o -> JSON.parseObject(o.toString(), ModuleOptionsBO.class)).collect(Collectors.toList());
					v.forEach(o -> result.add(o.getKey()));
				} else {
					ModuleOptionsBO v = JSON.parseObject(value.toString(), ModuleOptionsBO.class);
					result.add(v.getKey());
				}
				break;
			}
			case CHECKBOX:
			case TAG: {
				if (isList) {
					List<ModuleOptionsBO> v = (List<ModuleOptionsBO>) ((ArrayList) value).stream().map(o -> JSON.parseObject(o.toString(), ModuleOptionsBO.class)).collect(Collectors.toList());
					v.forEach(o -> result.add(o.getKey()));
				} else {
					List<ModuleOptionsBO> v = JSON.parseArray(value.toString(), ModuleOptionsBO.class);
					v.forEach(o -> result.add(o.getKey()));
				}
				break;
			}
			case AREA_POSITION:{
				if (isList) {
					List<CommonESNestedBO> v = (List<CommonESNestedBO>) ((ArrayList) value).stream().map(o -> JSON.parseObject(o.toString(), CommonESNestedBO.class)).collect(Collectors.toList());
					v.forEach(o -> result.add(o.getCode()));
				} else {
					List<CommonESNestedBO> v = JSON.parseArray(value.toString(), CommonESNestedBO.class);
					v.forEach(o -> result.add(o.getCode()));
				}
				break;
			}
			default:{
				if (isList) {
					List<String> v = (List<String>) ((ArrayList) value).stream().map(String::valueOf).collect(Collectors.toList());
					result.addAll(v);
				} else {
					result.add(value.toString());
				}
				break;
			}
		}
		Collections.sort(result);
		return result;
	}

	default String parseValue2StringByType(ModuleFieldEnum fieldEnum, String value, Boolean sourceFlag, Integer flowType) {
		if (ObjectUtil.isEmpty(value)) {
			return "";
		}
		switch (fieldEnum) {
			case DATA_UNION:
			case DATA_UNION_MULTI:
				if (!sourceFlag) {
					String multiMainValue = ApplicationContextHolder.getBean(IModuleFieldDataService.class).queryMultipleMainFieldValue(value);
					return multiMainValue;
				} else {
					if (ObjectUtil.equal(FlowTypeEnum.FILL.getType(), flowType)) {
						String multiMainValue = ApplicationContextHolder.getBean(IModuleFieldDataService.class).queryMultipleMainFieldValue(value);
						return multiMainValue;
					} else if (ObjectUtil.equal(FlowTypeEnum.UPDATE.getType(), flowType)) {
						JSONObject obj = JSONObject.parseObject(value);
						String fieldData = obj.getString("fieldData");
						List<ModuleFieldData> data = JSONObject.parseArray(fieldData, ModuleFieldData.class);
						String resValue = data.stream().map(ModuleFieldData::getValue).collect(Collectors.joining(Const.SEPARATOR));
						return resValue;
					}
				}
				return value;
			case USER:
				if (!sourceFlag) {
					List<Long> ids = Convert.toList(Long.class, value);
					value = ids.stream().map(UserCacheUtil::getUserName).collect(Collectors.joining(Const.SEPARATOR));
					return value;
				} else {
					if (ObjectUtil.equal(FlowTypeEnum.FILL.getType(), flowType)) {
						List<Long> ids = Convert.toList(Long.class, value);
						value = ids.stream().map(UserCacheUtil::getUserName).collect(Collectors.joining(Const.SEPARATOR));
						return value;
					}
				}
				List<SimpleUser> simpleUsers = JSON.parseArray(value, SimpleUser.class);
				value = simpleUsers.stream().map(SimpleUser::getUsername).collect(Collectors.joining(Const.SEPARATOR));
				return value;
			case SELECT:
				ModuleOptionsBO option = JSON.parseObject(value, ModuleOptionsBO.class);
				return option.getValue();
			case TAG:
			case CHECKBOX:
				List<ModuleOptionsBO> objs = JSON.parseArray(value, ModuleOptionsBO.class);
				return objs.stream().map(ModuleOptionsBO::getValue).collect(Collectors.joining(Const.SEPARATOR));
			case PERCENT:
				return value + "%";
			case DATE_INTERVAL:
				CommonESNestedBO dateFromTo = JSON.parseObject(value, CommonESNestedBO.class);
				return dateFromTo.getFromDate() + "至" + dateFromTo.getToDate();
			case AREA_POSITION:
				List<CommonESNestedBO> address = JSON.parseArray(value, CommonESNestedBO.class);
				return address.stream().map(CommonESNestedBO::getName).collect(Collectors.joining("-"));
			case CURRENT_POSITION:
				JSONObject json = JSON.parseObject(value);
				return json.getString("address");
			case STRUCTURE:
				if (!sourceFlag) {
					List<Long> ids = Convert.toList(Long.class, value);
					value = ids.stream().map(UserCacheUtil::getDeptName).collect(Collectors.joining(Const.SEPARATOR));
					return value;
				} else {
					if (ObjectUtil.equal(FlowTypeEnum.FILL.getType(), flowType)) {
						List<Long> ids = Convert.toList(Long.class, value);
						value = ids.stream().map(UserCacheUtil::getDeptName).collect(Collectors.joining(Const.SEPARATOR));
						return value;
					}
				}
				List<JSONObject> deptList = JSON.parseArray(value, JSONObject.class);
				value = deptList.stream().map(o -> o.getString("deptName")).collect(Collectors.joining(Const.SEPARATOR));
				return value;
			case ATTENTION:
				return EasyExcelParseUtil.attentionValue2Str(value);
			default:
				return value;
		}
	}

	/**
	 * 根据字段类型解析搜索条件内字段数据值为 jsonString
	 *
	 * @param fieldEnum
	 * @param values
	 * @return
	 */
	default String parJSONString(ModuleFieldEnum fieldEnum, List<String> values) {
		String value = "";
		switch (fieldEnum) {
			case CHECKBOX:
			case TAG:
			case AREA_POSITION: {
				List<JSONObject> jsonObjects = values.stream().map(s -> JSON.parseObject(s, JSONObject.class)).collect(Collectors.toList());
				value = JSON.toJSONString(jsonObjects);
				break;
			}
			case SELECT:
			case DATE_INTERVAL: {
				List<JSONObject> jsonObjects = values.stream().map(s -> JSON.parseObject(s, JSONObject.class)).collect(Collectors.toList());
				value = JSON.toJSONString(CollUtil.getFirst(jsonObjects));
				break;
			}
			default:
				value = values.get(0);
				break;
		}
		return value;
	}
}
