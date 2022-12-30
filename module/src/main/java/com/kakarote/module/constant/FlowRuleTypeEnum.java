package com.kakarote.module.constant;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-25 16:44
 */
public enum FlowRuleTypeEnum {
	CONDITION_CONDITION(1, "条件节点筛选条件"),
	UPDATE_CONDITION(2, "更新数据节点筛选条件"),
	UPDATE_UPDATE(3, "更新数据节点更新条件"),
	UPDATE_INSERT(4, "更新数据节点添加条件"),
	SAVE_INSERT(5, "添加数据节点添加条件"),
	NULL(999, null),
	;


	FlowRuleTypeEnum(Integer type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	private Integer type;
	private String desc;

	public Integer getType() {
		return type;
	}

	public static FlowRuleTypeEnum parse(Integer type) {
		return Arrays.stream(FlowRuleTypeEnum.values())
				.filter(e -> ObjectUtil.equal(type, e.getType()))
				.findFirst().orElse(NULL);
	}
}
