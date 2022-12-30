package com.kakarote.module.constant;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;

/**
 * @description: 统计字段类型
 * @author: zjj
 * @date: 2021-05-18 09:36
 */
public enum StatisticTypeEnum {
	COUNT(0, "count"),
	SUM(1, "sum"),
	MAX(2, "max"),
	MIN(3, "min"),
	AVERAGE(4, "average"),
	OLDEST(5, "oldest"),
	LATEST(6, "latest"),
	NULL(999, "NULL"),
	;

	StatisticTypeEnum(Integer code, String type) {
		this.code = code;
		this.type = type;
	}

	private static final StatisticTypeEnum[] AGG_TYPE = {COUNT, SUM, MAX, MIN, AVERAGE};

	public static boolean aggType(int code){
		if (Arrays.asList(AGG_TYPE).contains(parse(code))){
			return true;
		}
		return false;
	}

	public static StatisticTypeEnum parse(Integer code) {
		return Arrays.stream(StatisticTypeEnum.values())
				.filter(e -> ObjectUtil.equal(code, e.getCode()))
				.findFirst().orElse(NULL);
	}

	private Integer code;
	private String type;

	public Integer getCode() {
		return code;
	}

	public String getType() {
		return type;
	}
}
