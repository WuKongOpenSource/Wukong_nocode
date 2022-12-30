package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.BO.CommonConditionBO;
import com.kakarote.module.entity.BO.CustomNoticeReceiverSaveBO;
import com.kakarote.module.entity.BO.CustomNoticeSaveBO;
import com.kakarote.module.entity.PO.CustomNotice;
import com.kakarote.module.entity.PO.CustomNoticeReceiver;
import com.kakarote.module.mapper.CustomNoticeMapper;
import com.kakarote.module.service.ICustomNoticeReceiverService;
import com.kakarote.module.service.ICustomNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: CustomNoticeServiceImpl
 * @description: 自定义通知服务实现
 * @date 2022/3/22 16:52
 */
@Service
public class CustomNoticeServiceImpl extends BaseServiceImpl<CustomNoticeMapper, CustomNotice> implements ICustomNoticeService {

    @Autowired
    private ICustomNoticeReceiverService receiverService;

    @Override
    public List<CustomNotice> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery().eq(CustomNotice::getModuleId, moduleId).eq(CustomNotice::getVersion, version).list();
    }

    @Override
    public List<CustomNoticeSaveBO> queryList(Long moduleId, Integer version) {
        List<CustomNotice> notices = this.getByModuleIdAndVersion(moduleId, version);
        List<CustomNoticeReceiver> receivers = receiverService.getByModuleIdAndVersion(moduleId, version);
        Map<Long, CustomNoticeReceiver> noticeIdReceiveMap = receivers.stream().collect(Collectors.toMap(CustomNoticeReceiver::getNoticeId, Function.identity()));
        List<CustomNoticeSaveBO> result = new ArrayList<>();
        for (CustomNotice notice : notices) {
            CustomNoticeReceiver receiver = noticeIdReceiveMap.get(notice.getNoticeId());
            CustomNoticeSaveBO noticeSaveBO = this.buildNoticeSaveBO(notice, receiver);
            result.add(noticeSaveBO);
        }
        return result;
    }

    @Override
    public CustomNoticeSaveBO queryByNoticeId(Long noticeId, Integer version) {
        CustomNotice notice = this.getByNoticeId(noticeId, version);
        CustomNoticeReceiver receiver = receiverService.getByNoticeId(noticeId, version);
        return this.buildNoticeSaveBO(notice, receiver);
    }

    /**
     * 构造noticeSaveBO
     *
     * @param notice
     * @param receiver
     * @return
     */
    private CustomNoticeSaveBO buildNoticeSaveBO(CustomNotice notice, CustomNoticeReceiver receiver) {
        CustomNoticeSaveBO noticeSaveBO = BeanUtil.copyProperties(notice, CustomNoticeSaveBO.class,
                "updateFields","timeFieldConfig", "repeatPeriod", "effectConfig");
        if (ObjectUtil.isNotNull(receiver)) {
            CustomNoticeReceiverSaveBO receiverSaveBO = BeanUtil.copyProperties(receiver, CustomNoticeReceiverSaveBO.class);
            noticeSaveBO.setReceiveConfig(receiverSaveBO);
        }
        noticeSaveBO.setUpdateFields(Optional.ofNullable(notice.getUpdateFields())
                .map(v -> JSON.parseArray(v, Long.class)).orElse(null));
        noticeSaveBO.setTimeFieldConfig(Optional.ofNullable(notice.getTimeFieldConfig())
                .map(v -> JSON.parseObject(v, CustomNoticeSaveBO.CustomNoticeTimeFieldConfig.class)).orElse(null));
        noticeSaveBO.setRepeatPeriod(Optional.ofNullable(notice.getRepeatPeriod())
                .map(v -> JSON.parseObject(v, CustomNoticeSaveBO.CustomNoticeRepeatPeriod.class)).orElse(null));
        noticeSaveBO.setEffectConfig(Optional.ofNullable(notice.getEffectConfig())
                .map(v -> JSON.parseArray(v, CommonConditionBO.class)).orElse(null));
        return noticeSaveBO;
    }


    @Override
    public CustomNotice getByNoticeId(Long noticeId, Integer version) {
        return lambdaQuery().eq(CustomNotice::getNoticeId, noticeId).eq(CustomNotice::getVersion, version).one();
    }
}
