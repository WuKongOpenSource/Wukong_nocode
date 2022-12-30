package com.kakarote.module.service;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.MessageBO;
import com.kakarote.module.entity.BO.MessageDeleteBO;
import com.kakarote.module.entity.BO.MessageSetReadBO;
import com.kakarote.module.entity.PO.Message;
import com.kakarote.module.entity.VO.ModuleMessageVO;

import java.util.List;

/**
 * @author zjj
 * @title: IMessageService
 * @description: 系统消息
 * @date 2021/11/269:46
 */
public interface IMessageService extends BaseService<Message> {

    /**
     * 发送系统消息
     *
     * @param message
     */
    void sendMessage(MessageBO message);

    /**
     * 获取消息列表
     *
     * @return
     */
    List<ModuleMessageVO> queryMessageList();

    /**
     * 设置消息已读
     *
     * @param setReadBO
     */
    void setMessageRead(MessageSetReadBO setReadBO);

    /**
     * 删除消息
     *
     * @param deleteBO
     */
    void deleteMessage(MessageDeleteBO deleteBO);

    /**
     * 导入时
     * 保存一条信息
     *
     * @param message
     */
    void saveOneMessage(Message message);


    /**
     *
     * @param moduleId
     * @return
     */
    BasePage<Message> queryMsgList(Long moduleId, Integer type);
}
