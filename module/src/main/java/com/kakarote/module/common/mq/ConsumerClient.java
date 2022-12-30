package com.kakarote.module.common.mq;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.kakarote.module.constant.MessageTagEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-19 17:45
 */
@Configuration
public class ConsumerClient {
	@Autowired
	private MqConfig mqConfig;

	@Autowired
	private MqMessageListener messageListener;

	@Bean(initMethod = "start", destroyMethod = "shutdown")
	public ConsumerBean buildConsumer() {
		ConsumerBean consumerBean = new ConsumerBean();
		//配置文件
		Properties properties = mqConfig.getProperties();
		//将消费者线程数固定为20个 20为默认值
		properties.setProperty(PropertyKeyConst.ConsumeThreadNums, "20");
		consumerBean.setProperties(properties);
		//订阅消息
		Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
		//订阅普通消息
		Subscription subscription = new Subscription();
		subscription.setTopic(mqConfig.getTopicOne());
		subscription.setExpression(MessageTagEnum.EXPRESSION);
		subscriptionTable.put(subscription, messageListener);

		consumerBean.setSubscriptionTable(subscriptionTable);
		return consumerBean;
	}
}
