package com.kakarote.module.constant;

/**
 * @description: 操作类型
 * @author: zjj
 * @date: 2021-05-10 11:00
 */
public enum ActionTypeEnum {
	INSERT(0, "新建"),
	UPDATE(1, "编辑"),
	DELETE(2, "删除"),
	TRANSFER(4, "转移负责人"),
	ADD_TEAM_MEMBER(5, "添加团队成员"),
	REMOVE_TEAM_MEMBER(6, "移除团队成员"),
	PASS(7, "通过"),
	REJECT(8, "拒绝"),
	RECHECK(9, "撤回"),
	NULL(99, null)
	;

	private int code;
	private String msg;

	ActionTypeEnum(Integer code, String msg) {
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
