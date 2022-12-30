package com.kakarote.module.constant;

/**
 * @description: 默认值类型
 * @author: zjj
 * @date: 2021-05-11 18:17
 */
public enum DefaultValueTypeEnum {
	TYPE_1(1, "自定义"),
	TYPE_2(2, "关联"),
	TYPE_3(3, "公式"),
	;
	private int code;
	private String msg;

	DefaultValueTypeEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}
