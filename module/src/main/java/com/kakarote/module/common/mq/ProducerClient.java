package com.kakarote.module.common.mq;

import com.aliyun.openservices.ons.api.bean.ProducerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-19 17:10
 */
@Configuration
public class ProducerClient {

	@Autowired
	private MqConfig mqConfig;

	@Bean(initMethod = "start", destroyMethod = "shutdown")
	public ProducerBean buildProducer() {
		ProducerBean producer = new ProducerBean();
		producer.setProperties(mqConfig.getProperties());
		return producer;
	}
}
