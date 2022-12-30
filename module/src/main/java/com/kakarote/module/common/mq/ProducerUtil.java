package com.kakarote.module.common.mq;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.entity.BO.MsgBodyBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-20 17:57
 */
@Slf4j
@Component
public class ProducerUtil {

	@Autowired
	private ProducerBean producer;

	@Autowired MqConfig mqConfig;

	/**
	 * 发送消息到 topic1
	 *
	 * @param msg
	 * @return
	 */
	public Boolean sendMsgToTopicOne(MsgBodyBO msg) {
		try {
			sendTimeMsg(mqConfig.getTopicOne(), msg.getMsgTag().getTag(), msg.getMsgKey(),
					JSONUtil.toJsonStr(msg).getBytes(StandardCharsets.UTF_8), msg.getDelayTime());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 同步发送消息
	 *
	 * @param topic
	 * @param msgTag
	 * @param msgKey
	 * @param messageBody
	 * @return
	 */
	public SendResult sendMsg(String topic, String msgTag, String msgKey, byte[] messageBody) {
		Message msg = new Message(topic, msgTag, msgKey, messageBody);
		log.info("MQ-Send:{}", msg);
		return send(msg, false);
	}

	/**
	 * 同步发送定时/延时消息
	 *
	 * @param topic
	 * @param msgTag    标签，可用于消息小分类标注，对消息进行再归类
	 * @param msgKey    消息key值，建议设置全局唯一值，可不设置，不影响消息收发
	 * @param messageBody    消息body内容，生产者自定义内容，二进制形式的数据
	 * @param delayTime    服务端发送消息时间，立即发送输入0或比更早的时间，延时时间单位为毫秒（ms）
	 * @return
	 */
	public SendResult sendTimeMsg(String topic, String msgTag, String msgKey, byte[] messageBody, Long delayTime) {
		Message msg = new Message(topic, msgTag, msgKey, messageBody);
		if (ObjectUtil.isNotNull(delayTime)) {
			delayTime = System.currentTimeMillis() + delayTime;
			msg.setStartDeliverTime(delayTime);
		}
		log.info("MQ-Send:{}", msg);
		return send(msg, false);
	}

	/**
	 * 发送单向消息
	 *
	 * @param topic
	 * @param msgTag
	 * @param msgKey
	 * @param messageBody
	 */
	public void sendOneWayMsg(String topic, String msgTag, String msgKey, byte[] messageBody) {
		Message msg = new Message(topic, msgTag, msgKey, messageBody);
		this.send(msg, Boolean.TRUE);
	}

	/**
	 *普通消息发送发放
	 *
	 * @param msg
	 * @param isOneWay 是否单向发送
	 * @return
	 */
	private SendResult send(Message msg, Boolean isOneWay) {
		try {
			if (isOneWay) {
				producer.sendOneway(msg);
				return null;
			} else {
				SendResult sendResult = producer.send(msg);
				if (ObjectUtil.isNotNull(sendResult)) {
					return sendResult;
				} else {
					throw new BusinessException(ModuleCodeEnum.SEND_MSG_FAILED);
				}
			}
		}catch (Exception e) {
			throw new BusinessException(ModuleCodeEnum.SEND_MSG_FAILED);
		}
	}

}
