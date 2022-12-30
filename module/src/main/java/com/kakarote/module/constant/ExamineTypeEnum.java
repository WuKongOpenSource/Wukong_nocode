package com.kakarote.module.constant;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Arrays;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-25 11:12
 */
public enum ExamineTypeEnum {
	/**
	 * 指定成员审批
	 */
	MEMBER(1),
	/**
	 * 负责人主管审批
	 */
	SUPERIOR(2),
	/**
	 * 指定角色审批
	 */
	ROLE(3),
	/**
	 * 发起人自选成员审批
	 */
	OPTIONAL(4),
	/**
	 * 连续多级主管审批
	 */
	CONTINUOUS_SUPERIOR(5),

	/**
	 * 管理员审批，只在找不到审批对象时存在
	 */
	MANAGER(6),

	NULL(999),
	;

	ExamineTypeEnum(Integer type) {
		this.type = type;
	}

	public static ExamineTypeEnum parse(Integer type) {
		return Arrays.stream(ExamineTypeEnum.values())
				.filter(e -> ObjectUtil.equal(type, e.getType()))
				.findFirst().orElse(NULL);
	}

	private Integer type;

	public Integer getType() {
		return type;
	}

	public String getServiceName() {
		return StrUtil.toCamelCase(name().toLowerCase()) + "Service";
	}
}
