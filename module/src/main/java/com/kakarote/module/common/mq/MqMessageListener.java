package com.kakarote.module.common.mq;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.kakarote.common.redis.Redis;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.constant.MessageTagEnum;
import com.kakarote.module.entity.BO.MsgBodyBO;
import com.kakarote.module.entity.PO.FlowExamineRecord;
import com.kakarote.module.service.*;
import com.kakarote.module.service.impl.FlowExamineServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-19 17:39
 */
@Slf4j
@Component
public class MqMessageListener implements MessageListener, ModulePageService {

	@Autowired
	private IModuleStatisticFieldUnionService statisticFieldUnionService;

	@Autowired
	private IModuleFieldDataService fieldDataService;

	@Autowired
	private IFlowDealDataService dealDataService;

	@Autowired
	private IFlowFillService flowFillService;

	@Autowired
	private FlowExamineServiceImpl examineService;

	@Autowired
	private IFlowExamineRecordService examineRecordService;

    @Autowired
    private ICustomNoticeRecordService noticeRecordService;

	@Autowired
	private IModuleFieldFormulaProvider fieldFormulaProvider;

	@Autowired
	private Redis redis;

	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
		// 唯一key
		String msgKey = message.getKey();
		// 消息类型
		String tag = message.getTag();
		String body = new String(message.getBody());
		log.info("MQ-Receiver:{}", body);
		if(!redis.setNx(msgKey, 5L, 1)){
			return Action.ReconsumeLater;
		}
		try {
			MessageTagEnum tagEnum = MessageTagEnum.parse(tag);
			MsgBodyBO bodyBO = JSONUtil.toBean(body, MsgBodyBO.class);
			UserUtil.setUser(bodyBO.getUserId());
			switch (tagEnum) {
				case INSERT_DATA:
				case UPDATE_DATA:
					// 更新统计字段的值
					statisticFieldUnionService.updateStatisticFieldValue(bodyBO.getModuleId(), bodyBO.getVersion());
                    // 保存自定义通知记录
                    noticeRecordService.saveNoticeRecord(bodyBO);
					// 更新计算公式字段值
					fieldFormulaProvider.updateFormulaFieldValue(bodyBO.getModuleId());
					break;
				case DELETE_DATA:
					// 更新统计字段的值
					statisticFieldUnionService.updateStatisticFieldValue(bodyBO.getModuleId(), bodyBO.getVersion());
					// 更新计算公式字段值
					fieldFormulaProvider.updateFormulaFieldValue(bodyBO.getModuleId());
					break;
				case DELETE_FIELD:
					// 删除es
					for (Long fieldId : bodyBO.getFileIds()) {
						List<Long> dataIds = fieldDataService.getDataIdsByFieldId(fieldId, bodyBO.getModuleId());
						deletePage(dataIds, bodyBO.getModuleId());
					}
					// 更新统计字段的值
					statisticFieldUnionService.updateStatisticFieldValue(bodyBO.getModuleId(), bodyBO.getVersion());
					// 更新计算公式字段值
					fieldFormulaProvider.updateFormulaFieldValue(bodyBO.getModuleId());
					break;
				case DELETE_MODULE:
					// 删除
                    if (CollUtil.isNotEmpty(bodyBO.getModuleIds())) {
                        for (Long moduleId : bodyBO.getModuleIds()) {
                            deleteByModuleId(moduleId);
                            noticeRecordService.deleteByModuleId(moduleId);
                        }
                    }
					break;
				case DEAL_FLOW:
					// 节点处理
					Long recordId = bodyBO.getRecordId();
					FlowExamineRecord record = examineRecordService.getById(recordId);
					if (ObjectUtil.isNull(record)) {
						return Action.ReconsumeLater;
					}
					dealDataService.dealDataFlow(record, bodyBO.getFlowId());
					break;
				case FILL_TIME_LIMIT:
					flowFillService.dealTimeFlow(bodyBO);
					break;
				case EXAMINE_TIME_LIMIT:
					examineService.dealTimeFlow(bodyBO);
					break;
				default:
					return Action.ReconsumeLater;
			}
			log.info("MQ-Commit:{}", body);
			return Action.CommitMessage;
		} catch (Exception e) {
			log.error("MQ-Error", e);
			return Action.ReconsumeLater;
		} finally {
			UserUtil.removeUser();
		}
	}
}
