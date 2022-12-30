package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.module.entity.BO.CustomNoticeReceiverSaveBO;
import com.kakarote.module.entity.PO.CustomNoticeReceiver;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.mapper.CustomNoticeReceiverMapper;
import com.kakarote.module.service.ICustomNoticeReceiverService;
import com.kakarote.module.service.IModuleFieldDataCommonService;
import com.kakarote.module.service.IModuleFieldDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zjj
 * @title: CustomNoticeReceiverServiceImpl
 * @description: 自定义提醒接收人
 * @date 2022/3/23 10:02
 */
@Service
public class CustomNoticeReceiverServiceImpl extends BaseServiceImpl<CustomNoticeReceiverMapper, CustomNoticeReceiver> implements ICustomNoticeReceiverService {

    @Autowired
    private IModuleFieldDataCommonService dataCommonService;

    @Autowired
    private IModuleFieldDataService fieldDataService;

    @Autowired
    private UserService userService;

    @Override
    public List<CustomNoticeReceiver> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery().eq(CustomNoticeReceiver::getModuleId, moduleId)
                .eq(CustomNoticeReceiver::getVersion, version).list();
    }

    @Override
    public CustomNoticeReceiver getByNoticeId(Long noticeId, Integer version) {
        return lambdaQuery().eq(CustomNoticeReceiver::getNoticeId, noticeId)
                .eq(CustomNoticeReceiver::getVersion, version).one();
    }

    @Override
    public Set<Long> getReceivers(CustomNoticeReceiverSaveBO receiverSaveBO, Long dataId) {
        Set<Long> receiverIds = new HashSet<>();
        ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(dataId);
        if (ObjectUtil.isNull(dataCommon)) {
            return receiverIds;
        }
        List<UserInfo> userInfos = userService.queryUserInfoList().getData();
        if (receiverSaveBO.getNoticeCreator()) {
            receiverIds.add(dataCommon.getCreateUserId());
        }
        if (receiverSaveBO.getNoticeOwner()) {
            receiverIds.add(dataCommon.getOwnerUserId());
        }
        if (CollUtil.isNotEmpty(receiverSaveBO.getNoticeUser())) {
            receiverIds.addAll(receiverSaveBO.getNoticeUser());
        }
        if (CollUtil.isNotEmpty(receiverSaveBO.getNoticeRole())) {
            for (Long roleId : receiverSaveBO.getNoticeRole()) {
                receiverIds.addAll(queryUserByRoleId(userInfos, roleId));
            }
        }
        Map<Long, Object> fieldIdDataMap = fieldDataService.queryFieldIdDataMap(dataId);
        // 人员字段
        if (CollUtil.isNotEmpty(receiverSaveBO.getUserField())) {
            for (Long fieldId : receiverSaveBO.getUserField()) {
                Long receiverId = MapUtil.getLong(fieldIdDataMap, fieldId);
                if (ObjectUtil.isNotNull(receiverId)) {
                    receiverIds.add(receiverId);
                }
            }
        }
        // 部门字段
        if (CollUtil.isNotEmpty(receiverSaveBO.getDeptField())) {
            Set<Long> deptIds = new HashSet<>();
            for (Long fieldId : receiverSaveBO.getDeptField()) {
                Long deptId = MapUtil.getLong(fieldIdDataMap, fieldId);
                if (ObjectUtil.isNotNull(deptId)) {
                    deptIds.add(deptId);
                }
            }
            if (CollUtil.isNotEmpty(deptIds)) {
                List<Long> userIds = userService.queryUserByDeptIds(deptIds).getData();
                receiverIds.addAll(userIds);
            }
        }

        if (CollUtil.isNotEmpty(receiverSaveBO.getParentLevel())) {
            for (Integer level : receiverSaveBO.getParentLevel()) {
                Long parentUserId = queryParentUserByLevel(dataCommon.getOwnerUserId(), level);
                if (ObjectUtil.isNotNull(parentUserId) && ObjectUtil.notEqual(0L, parentUserId)) {
                    receiverIds.add(parentUserId);
                }
            }
        }
        return receiverIds;
    }
}
