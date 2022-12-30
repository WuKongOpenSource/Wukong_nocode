package com.kakarote.module.constant;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;

/**
 * @description: 流程状态
 * @author: zjj
 * @date: 2021-06-01 13:48
 */
public enum FlowStatusEnum {
	/**
	 * 待处理
	 */
	WAIT(0),

	/**
	 * 已处理
	 */
	PASS(1),

	/**
	 * 拒绝
	 */
	REJECT(2),

	/**
	 * 处理中
	 */
	DEALING(3),

	/**
	 * 撤回
	 */
	RECHECK(4),

	/**
	 * 未提交
	 */
	UN_SUBMITTED(5),

	/**
	 * 失败
	 */
	FAILED(7),

	/**
	 * 作废
	 */
	INVALID(8),

	/**
	 * 忽略
	 */
	DEFAULT(9),
	;

	private Integer status;

	public Integer getStatus() {
		return status;
	}

	FlowStatusEnum(Integer status) {
		this.status = status;
	}

	private static final FlowStatusEnum[] EXAMINE_STATUS_ENUMS = {REJECT, RECHECK, UN_SUBMITTED, DEFAULT};
	private static final FlowStatusEnum[] EXAMINE_STATUS_TOP_ENUMS = {REJECT, RECHECK, PASS, INVALID};

	public static boolean editable(Integer status) {
		if (ObjectUtil.isNull(status) || Arrays.asList(EXAMINE_STATUS_ENUMS).contains(parse(status))) {
			return true;
		}
		return false;
	}

	public static boolean stopFlow(Integer status) {
		if (Arrays.asList(EXAMINE_STATUS_TOP_ENUMS).contains(parse(status))) {
			return true;
		}
		return false;
	}

	public static FlowStatusEnum parse(Integer status) {
		return Arrays.stream(FlowStatusEnum.values())
				.filter(e -> ObjectUtil.equal(status, e.getStatus()))
				.findFirst().orElse(DEFAULT);
	}
}
