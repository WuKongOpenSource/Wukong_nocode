package com.kakarote.module.constant;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-19 18:07
 */
public enum MessageTagEnum {
	INSERT_DATA(0, "INSERT_DATA", "插入数据"),
	UPDATE_DATA(1, "UPDATE_DATA", "更新数据"),
	DELETE_DATA(2, "DELETE_DATA", "删除数据"),
	DELETE_FIELD(3, "DELETE_FIELD", "删除字段"),
	DELETE_MODULE(4, "DELETE_MODULE", "删除模块"),
	FILL_TIME_LIMIT(5, "FILL_TIME_LIMIT", "填写节点限时"),
	EXAMINE_TIME_LIMIT(6, "EXAMINE_TIME_LIMIT", "审批节点限时"),
	DEAL_FLOW(7, "DEAL_FLOW", "节点处理"),
	NULL(999, null, null),
	;

	MessageTagEnum(Integer code, String tag, String desc) {
		this.code = code;
		this.tag = tag;
		this.desc = desc;
	}

	public static MessageTagEnum parse(String tag) {
		return Arrays.stream(MessageTagEnum.values())
				.filter(e -> ObjectUtil.equal(tag, e.getTag()))
				.findFirst().orElse(NULL);
	}

	public static final String EXPRESSION = "INSERT_DATA || UPDATE_DATA || DELETE_DATA || DELETE_FIELD || DELETE_MODULE " +
			"|| DEAL_FLOW || FILL_TIME_LIMIT || EXAMINE_TIME_LIMIT || PUBLISH_MODULE";


	private Integer code;
	private String tag;
	private String desc;

	public Integer getCode() {
		return code;
	}

	public String getTag() {
		return tag;
	}

}
