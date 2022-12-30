package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.constant.MessageTagEnum;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.CustomNoticeRecord;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.mapper.CustomNoticeRecordMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: CustomNoticeRecordServiceImpl
 * @description: 自定义提醒记录服务接口实现
 * @date 2022/3/23 17:14
 */
@Service
public class CustomNoticeRecordServiceImpl extends BaseServiceImpl<CustomNoticeRecordMapper, CustomNoticeRecord> implements ICustomNoticeRecordService, IFlowCommonService {

    @Autowired
    private ICustomNoticeService customNoticeService;

    @Autowired
    private ICustomNoticeReceiverService receiverService;

    @Autowired
    private IModuleFieldDataService fieldDataService;

    @Override
    public List<CustomNoticeRecord> queryList(Integer status, Integer limit) {
        List<CustomNoticeRecord> noticeRecordList = baseMapper.queryList(status, limit);
        return noticeRecordList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveNoticeRecord(MsgBodyBO msgBodyBO) {
        Long moduleId = msgBodyBO.getModuleId();
        Long dataId = msgBodyBO.getDataId();
        MessageTagEnum tagEnum = msgBodyBO.getMsgTag();
        ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
        if (ObjectUtil.isNull(module)) {
            return;
        }
        List<CustomNoticeSaveBO> notices = customNoticeService.queryList(module.getModuleId(), module.getVersion());
        if (CollUtil.isNotEmpty(notices)) {
            List<CustomNoticeRecord> records = new ArrayList<>();
            List<ModuleFieldValueBO> oldData = msgBodyBO.getOldData();
            List<ModuleFieldValueBO> currentData = msgBodyBO.getCurrentData();

            Map<Long, String> oldFieldIdValueMap = oldData.stream().collect(Collectors.toMap(ModuleFieldValueBO::getFieldId, ModuleFieldValueBO::getValue));
            Map<Long, String> currentFieldIdValueMap = currentData.stream().collect(Collectors.toMap(ModuleFieldValueBO::getFieldId, d -> Optional.ofNullable(d.getValue()).orElse("")));

            // 字段值转换
            List<ModuleFieldValueBO> fieldValueBOS = fieldDataService.transFieldValue(moduleId, module.getVersion(), currentData);
            for (CustomNoticeSaveBO notice : notices) {
                CustomNoticeRecord record = new CustomNoticeRecord();
                record.setDataId(dataId);
                record.setNoticeId(notice.getNoticeId());
                record.setStatus(0);
                // 接收人配置
                CustomNoticeReceiverSaveBO receiver = notice.getReceiveConfig();
                if (ObjectUtil.isNull(receiver)) {
                    continue;
                }
                Set<Long> receivers = receiverService.getReceivers(receiver, dataId);

                if (CollUtil.isEmpty(receivers)) {
                    continue;
                }
                List<CommonConditionBO> conditionBOS = notice.getEffectConfig();
                Boolean isPass = commonConditionPass(conditionBOS, currentData, moduleId, module.getVersion());
                if (!isPass) {
                    continue;
                }
                if (Arrays.asList(0, 1, 2).contains(notice.getEffectType())) {
                    // 新增数据
                    if (ObjectUtil.equal(0, notice.getEffectType())) {
                        if (ObjectUtil.equal(MessageTagEnum.INSERT_DATA, tagEnum)) {
                            record.setStatus(1);
                        } else {
                            continue;
                        }
                    } else if (ObjectUtil.equal(1, notice.getEffectType())) {
                        // 更新数据
                        if (ObjectUtil.equal(MessageTagEnum.UPDATE_DATA, tagEnum)) {
                            record.setStatus(1);
                        } else {
                            continue;
                        }
                    } else if (ObjectUtil.equal(2, notice.getEffectType())) {
                        // 更新指定字段
                        if (ObjectUtil.equal(MessageTagEnum.UPDATE_DATA, tagEnum)) {
                            List<Long> updateFields = notice.getUpdateFields();
                            Long fieldId = updateFields.stream().filter(i -> !StrUtil.equals(oldFieldIdValueMap.get(i), currentFieldIdValueMap.get(i))).findFirst().orElse(null);
                            if (ObjectUtil.isNull(fieldId)) {
                                continue;
                            }
                            record.setStatus(1);
                        } else {
                            continue;
                        }
                    }
                    // 消息内容
                    String content = receiver.getContent();
                    // 发送消息
                    MessageBO messageBO = new MessageBO();
                    messageBO.setDataId(record.getDataId());
                    messageBO.setValue(content);
                    messageBO.setModuleId(module.getModuleId());
                    messageBO.setModuleName(module.getName());
                    messageBO.setTypeId(notice.getNoticeId());
                    messageBO.setTypeName(notice.getNoticeName());
                    messageBO.setType(2);
                    messageBO.setReceivers(receivers);
                    messageBO.setCreateUserId(0L);
                    messageBO.setExtData(new JSONObject().fluentPut("entity", fieldValueBOS).toJSONString());
                    IMessageService messageService = ApplicationContextHolder.getBean(IMessageService.class);
                    messageService.sendMessage(messageBO);
                }
                record.setModuleId(moduleId);
                record.setVersion(module.getVersion());
                record.setBatchId(IdUtil.simpleUUID());
                record.setCreateTime(LocalDateTime.now());
                records.add(record);
            }
            if (CollUtil.isNotEmpty(records)) {
                saveBatch(records);
            }
        }
    }

    @Override
    public void deleteByModuleId(Long moduleId) {
        lambdaUpdate().eq(CustomNoticeRecord::getModuleId, moduleId).remove();
    }
}
