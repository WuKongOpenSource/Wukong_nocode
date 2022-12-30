package com.kakarote.module.common.mq;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-18 10:53
 */
@Configuration
@ConfigurationProperties(prefix = "rocketmq")
public class MqConfig {
	private String accessKey;
	private String secretKey;
	private String nameSrvAddr;
	private String topicOne;
	private String topicTwo;
	private String groupId;


	public Properties getProperties() {
		Properties properties = new Properties();
		properties.setProperty(PropertyKeyConst.AccessKey, this.accessKey);
		properties.setProperty(PropertyKeyConst.SecretKey, this.secretKey);
		properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, this.nameSrvAddr);
		properties.setProperty(PropertyKeyConst.GROUP_ID, this.groupId);
		properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "4000");
		return properties;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getNameSrvAddr() {
		return nameSrvAddr;
	}

	public void setNameSrvAddr(String nameSrvAddr) {
		this.nameSrvAddr = nameSrvAddr;
	}

	public String getTopicOne() {
		return topicOne;
	}

	public void setTopicOne(String topicOne) {
		this.topicOne = topicOne;
	}

	public String getTopicTwo() {
		return topicTwo;
	}

	public void setTopicTwo(String topicTwo) {
		this.topicTwo = topicTwo;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
