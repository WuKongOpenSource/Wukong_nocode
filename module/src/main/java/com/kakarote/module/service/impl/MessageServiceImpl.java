package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.constant.ModuleType;
import com.kakarote.module.entity.BO.MessageBO;
import com.kakarote.module.entity.BO.MessageDeleteBO;
import com.kakarote.module.entity.BO.MessageSetReadBO;
import com.kakarote.module.entity.PO.Message;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleMetadata;
import com.kakarote.module.entity.VO.ModuleMessageVO;
import com.kakarote.module.mapper.MessageMapper;
import com.kakarote.module.service.IMessageService;
import com.kakarote.module.service.IModuleMetadataService;
import com.kakarote.module.service.IModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: MessageServiceImpl
 * @description: 系统消息
 * @date 2021/11/269:46
 */
@Service
public class MessageServiceImpl extends BaseServiceImpl<MessageMapper, Message> implements IMessageService {

    @Autowired
    private IModuleMetadataService metadataService;

    @Autowired
    private IModuleService moduleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(MessageBO message) {
        if (CollUtil.isEmpty(message.getReceivers())) {
            return;
        }
        List<Message> messages = new ArrayList<>();
        for (Long receiver : message.getReceivers()) {
            if (ObjectUtil.equal(0L, receiver)) {
                continue;
            }
            Message m = JSON.parseObject(JSON.toJSONString(message), Message.class);
            m.setReceiver(receiver);
            m.setCreateTime(DateUtil.date());
            messages.add(m);
        }
        saveBatch(messages);
    }

    @Override
    public List<ModuleMessageVO> queryMessageList() {
        List<ModuleMessageVO> result = new ArrayList<>();
        List<ModuleMetadata> metadataList = metadataService.lambdaQuery()
                .orderByDesc(ModuleMetadata::getCreateTime).list();
        if (CollUtil.isEmpty(metadataList)) {
            return result;
        }
        List<ModuleEntity> modules = moduleService.lambdaQuery()
                .eq(ModuleEntity::getIsActive, true)
                .eq(ModuleEntity::getStatus, 1)
                .eq(ModuleEntity::getModuleType, ModuleType.MODULE.getType())
                .orderByDesc(ModuleEntity::getCreateTime)
                .list();
        Map<Long, List<ModuleEntity>> moduleGroupByAppId = modules.stream().collect(Collectors.groupingBy(ModuleEntity::getApplicationId));
        List<Message> allMessages = lambdaQuery()
                .eq(Message::getReceiver, UserUtil.getUserId())
                .orderByDesc(Message::getCreateTime)
                .list();
        for (ModuleMetadata metadata : metadataList) {
            if (!moduleGroupByAppId.containsKey(metadata.getApplicationId())) {
                continue;
            }
            ModuleMessageVO vo = new ModuleMessageVO();
            vo.setApplicationId(metadata.getApplicationId());
            vo.setName(metadata.getName());
            List<Long> moduleIds = moduleGroupByAppId.get(metadata.getApplicationId()).stream().map(ModuleEntity::getModuleId).collect(Collectors.toList());
            List<ModuleMessageVO.MessageVO> msg = allMessages
                    .stream()
                    .filter(m -> moduleIds.contains(m.getModuleId()))
                    .map(m -> {
                        ModuleMessageVO.MessageVO messageVO = new ModuleMessageVO.MessageVO();
                        BeanUtils.copyProperties(m, messageVO, "createUserId", "receiver");
                        messageVO.setCreateUser(UserCacheUtil.getSimpleUser(m.getCreateUserId()));
                        messageVO.setReceiverUser(UserCacheUtil.getSimpleUser(m.getReceiver()));
                        return messageVO;
                    })
                    .collect(Collectors.toList());
            vo.setMessages(msg);
            result.add(vo);
        }
        return result;

        // List<ModuleMessageVO> result = new ArrayList<>();
        // List<ModuleMetadata> metadataList = metadataService.lambdaQuery().eq(ModuleMetadata::getCompanyId, UserUtil.getCompanyId()).orderByDesc(ModuleMetadata::getCreateTime).list();
        // if (CollUtil.isEmpty(metadataList)) {
        //     return result;
        // }
        // List<Module> modules = moduleService.lambdaQuery()
        //         .eq(Module::getCompanyId, UserUtil.getCompanyId())
        //         .eq(Module::getIsActive, true)
        //         .eq(Module::getStatus, 1)
        //         .orderByDesc(Module::getCreateTime).list();
        // Map<Long, List<Module>> moduleGroupByQAppId = modules.stream().collect(Collectors.groupingBy(Module::getApplicationId));
        // List<Message> allMessages = lambdaQuery()
        //         .eq(Message::getReceiver, UserUtil.getUserId())
        //         .eq(Message::getCompanyId, UserUtil.getCompanyId()).orderByDesc(Message::getCreateTime).list();
        // Map<Long, List<Message>> messageGroupByModuleId = allMessages.stream().collect(Collectors.groupingBy(Message::getModuleId));
        // for (ModuleMetadata metadata : metadataList) {
        //     ModuleMessageVO vo = new ModuleMessageVO();
        //     vo.setApplicationId(metadata.getApplicationId());
        //     vo.setName(metadata.getName());
        //     List<Module> moduleList = moduleGroupByQAppId.get(metadata.getApplicationId());
        //     if (CollUtil.isNotEmpty(moduleList)) {
        //         for (Module module : moduleList) {
        //             ModuleMessageVO.ModuleIdNameMessageVO moduleIdNameMessageVO = new ModuleMessageVO.ModuleIdNameMessageVO();
        //             moduleIdNameMessageVO.setModuleId(module.getModuleId());
        //             moduleIdNameMessageVO.setModuleName(module.getName());
        //             List<Message> messages = messageGroupByModuleId.get(module.getModuleId());
        //             if (CollUtil.isNotEmpty(messages)) {
        //                 messages = messages.stream().sorted(Comparator.comparing(Message::getCreateTime).reversed()).collect(Collectors.toList());
        //                 for (Message message : messages) {
        //                     ModuleMessageVO.MessageVO messageVO = new ModuleMessageVO.MessageVO();
        //                     BeanUtils.copyProperties(message, messageVO, "createUserId", "receiver");
        //                     messageVO.setCreateUser(UserCacheUtil.getSimpleUser(message.getCreateUserId()));
        //                     messageVO.setReceiverUser(UserCacheUtil.getSimpleUser(message.getReceiver()));
        //
        //                     moduleIdNameMessageVO.getMessages().add(messageVO);
        //                 }
        //                 vo.getModules().add(moduleIdNameMessageVO);
        //             }
        //         }
        //     }
        //     result.add(vo);
        // }
        // return result;
    }

    @Override
    public void setMessageRead(MessageSetReadBO setReadBO) {
        if (setReadBO.getAllRead()) {
            lambdaUpdate()
                    .set(Message::getIsRead, 1)
                    .set(Message::getReadTime, DateUtil.date())
                    .update();
        } else {
            if (CollUtil.isNotEmpty(setReadBO.getMessageId())) {
                lambdaUpdate().set(Message::getIsRead, 1).set(Message::getReadTime, DateUtil.date())
                        .in(Message::getMessageId, setReadBO.getMessageId())
                        .update();
            }
        }
    }

    @Override
    public void deleteMessage(MessageDeleteBO deleteBO) {
        if (ObjectUtil.isNull(deleteBO)) {
            return;
        }
        if (deleteBO.getDeleteReadMessage()) {
            lambdaUpdate().eq(Message::getIsRead, 1)
                    .eq(Message::getReceiver, UserUtil.getUserId())
                    .remove();
        } else {
            if (CollUtil.isNotEmpty(deleteBO.getMessageId())) {
                lambdaUpdate().in(Message::getMessageId, deleteBO.getMessageId()).remove();
            }
        }
    }

    @Override
    public void saveOneMessage(Message message) {
        this.saveOrUpdate(message);
    }

    @Override
    public BasePage<Message> queryMsgList(Long moduleId, Integer type) {
        PageEntity pageEntity = new PageEntity();
        BasePage<Message> list = this.getBaseMapper().getImportHistoryList(pageEntity.parse(), moduleId, type, UserUtil.getUserId());
        list.getList().forEach(data -> data.setCreateUserName(UserCacheUtil.getUserName(data.getCreateUserId())));
        return list;
    }

}
