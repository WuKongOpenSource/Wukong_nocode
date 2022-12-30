package com.kakarote.module.common;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @description:
 * @author: zjj
 * @date: 2021-07-12 19:11
 */
@PropertySource(value = {"classpath:message*.properties"})
public class ResourcesUtil {

	/**
	 * 将国际化信息
	 */
	private static final Map<String, ResourceBundle> MESSAGES = new HashMap<>();

	/**
	 * 获取国际化信息
	 */
	public static String getMessage(String key, Object... params) {
		//获取语言，这个语言是从header中的Accept-Language中获取的，
		Locale locale = LocaleContextHolder.getLocale();
		if (ObjectUtil.isNull(locale)) {
			locale = Locale.CHINA;
		}
		ResourceBundle message = MESSAGES.get(locale.getLanguage());
		if (ObjectUtil.isNull(message)) {
			synchronized (MESSAGES) {
				//在这里读取配置信息
				message = MESSAGES.get(locale.getLanguage());
				if (ObjectUtil.isNull(message)) {
					message = ResourceBundle.getBundle("message", locale);
					MESSAGES.put(locale.getLanguage(), message);
				}
			}
		}
		//此处获取并返回message
		if (ObjectUtil.isNotNull(params)) {
			return MessageFormat.format(message.getString(key), params);
		}
		return message.getString(key);
	}

	/**
	 * 清除国际化信息
	 */
	public static void flushMessage() {
		MESSAGES.clear();
	}
}
