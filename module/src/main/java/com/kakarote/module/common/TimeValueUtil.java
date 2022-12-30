package com.kakarote.module.common;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: zjj
 * @date: 2021-07-20 16:57
 */
public class TimeValueUtil {

	public static final String MINUTE = "m";
	public static final String HOUR = "H";
	public static final String DAY = "D";
	public static final String WEEK = "W";
	public static final String MONTH = "M";
	public static final String YEAR = "Y";

	public static Long parseTimeMillis(String timeValue) {
		if (ObjectUtil.isNull(timeValue)) {
			return 0L;
		}
		String strValue = Pattern.compile("[A-Za-z]").matcher(timeValue).replaceAll("").trim();
		BigDecimal bigDecimal = new BigDecimal(strValue);
		if (timeValue.contains(DAY.toLowerCase())) {
			return bigDecimal.multiply(BigDecimal.valueOf(24 * 60 * 60 * 1000)).longValue();
		} else if (timeValue.contains(HOUR.toLowerCase())) {
			return bigDecimal.multiply(BigDecimal.valueOf(60 * 60 * 1000)).longValue();
		} else if (timeValue.contains(MINUTE)) {
			return bigDecimal.multiply(BigDecimal.valueOf(60 * 1000)).longValue();
		}
		return 0L;
	}

	/**
	 * 日期计算
	 *
	 * @param date
	 * @param value
	 * @param unit
	 * @return
	 */
	public static Date addTime(LocalDateTime date, Integer value, String unit){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.toInstant(ZoneOffset.of("+8")).toEpochMilli());
		if (StrUtil.equals(HOUR, unit)) {
			calendar.add(Calendar.HOUR, value);
		} else if (StrUtil.equals(DAY, unit)) {
			calendar.add(Calendar.DATE, value);
		} else if (StrUtil.equals(WEEK, unit)) {
			calendar.add(Calendar.WEEK_OF_YEAR, value);
		} else if (StrUtil.equals(MONTH, unit)) {
			calendar.add(Calendar.MONTH, value);
		} else if (StrUtil.equals(YEAR, unit)) {
			calendar.add(Calendar.YEAR, value);
		}
		return calendar.getTime();
	}
}
