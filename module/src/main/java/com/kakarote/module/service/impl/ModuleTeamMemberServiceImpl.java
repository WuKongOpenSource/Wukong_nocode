package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.constant.ActionTypeEnum;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.entity.BO.MessageBO;
import com.kakarote.module.entity.BO.TeamMemberRemoveBO;
import com.kakarote.module.entity.BO.TeamMemberSaveBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleDataOperationRecord;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.entity.PO.ModuleTeamMember;
import com.kakarote.module.entity.VO.TeamMemberVO;
import com.kakarote.module.mapper.ModuleTeamMemberMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ModuleTeamMemberServiceImpl extends BaseServiceImpl<ModuleTeamMemberMapper, ModuleTeamMember> implements IModuleTeamMemberService, ModulePageService {

    @Autowired
    private IModuleService moduleService;

    @Autowired
    private IModuleFieldDataCommonService dataCommonService;

    @Autowired
    private IModuleFieldDataProvider fieldDataProvider;

    @Autowired
    private IModuleDataOperationRecordService operationRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTeamMember(TeamMemberSaveBO memberSaveBO) {
        if (CollUtil.isEmpty(memberSaveBO.getMemberIds())) {
            return;
        }
        if (ObjectUtil.isNull(memberSaveBO.getModuleId())) {
            throw new BusinessException(ModuleCodeEnum.MODULE_ID_IS_NULL_ERROR);
        }
        if (ObjectUtil.isNull(memberSaveBO.getDataId())) {
            throw new BusinessException(ModuleCodeEnum.DATA_ID_IS_NULL);
        }
        ModuleEntity module = moduleService.getNormal(memberSaveBO.getModuleId());
        // 获取模块主字段值
        String mainFieldValue = fieldDataProvider.queryValue(memberSaveBO.getDataId(), module.getMainFieldId());
        // 已有的团队成员
        List<ModuleTeamMember> members = this.getTeamMemberByDataId(memberSaveBO.getModuleId(), memberSaveBO.getDataId());
        Map<Long, ModuleTeamMember> existUsers = members.stream().collect(Collectors.toMap(ModuleTeamMember::getUserId, Function.identity()));
        ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(memberSaveBO.getDataId());
        List<ModuleTeamMember> teamMembers = new ArrayList<>();
        List<ModuleDataOperationRecord> operationRecords = new ArrayList<>();
        List<Long> receivers = new ArrayList<>();
        for (Long memberId : memberSaveBO.getMemberIds()) {
            // 已有的团队成员先删除再保存
            ModuleTeamMember existMember = existUsers.get(memberId);
            if (ObjectUtil.isNotNull(existMember)) {
                removeById(existMember.getId());
            }
            if (ObjectUtil.equal(dataCommon.getOwnerUserId(), memberId)) {
                continue;
            }
            ModuleTeamMember member = new ModuleTeamMember();
            member.setPower(memberSaveBO.getPower());
            member.setModuleId(memberSaveBO.getModuleId());
            member.setDataId(memberSaveBO.getDataId());
            member.setUserId(memberId);
            member.setCreateTime(DateUtil.date());
            member.setExpiresTime(memberSaveBO.getExpiresTime());
            member.setCreateUserId(UserUtil.getUserId());
            teamMembers.add(member);
            if (ObjectUtil.isNull(existMember)) {
                operationRecords.add(operationRecordService.initTeamUserEntity(module.getModuleId(), module.getVersion(), memberSaveBO.getDataId(),
                        mainFieldValue, memberId, ActionTypeEnum.ADD_TEAM_MEMBER));
                receivers.add(memberId);
            }
        }
        saveBatch(teamMembers);
        MessageBO messageBO = new MessageBO();
        messageBO.setDataId(memberSaveBO.getDataId());
        // UserUtil.getUser().getRealname() + "将您添加为"+ module.getName() + "《" + mainFieldValue + "》" + "的团队"
        messageBO.setValue(UserUtil.getUser().getNickname() + "将您添加为"+ module.getName());
        messageBO.setModuleId(module.getModuleId());
        messageBO.setModuleName(module.getName());
        messageBO.setTypeId(0L);
        messageBO.setTypeName(mainFieldValue);
        messageBO.setExtData(null);
        messageBO.setType(4);
        messageBO.setReceivers(receivers);
        messageBO.setCreateUserId(UserUtil.getUserId());
        ApplicationContextHolder.getBean(IMessageService.class).sendMessage(messageBO);
        operationRecordService.saveBatch(operationRecords);
        // 更新团队成员到 dataCommon
        List<ModuleTeamMember> memberList = this.getTeamMemberByDataId(dataCommon.getModuleId(), dataCommon.getDataId());
        List<Long> memberIds = memberList.stream().map(ModuleTeamMember::getUserId).collect(Collectors.toList());
        dataCommon.setTeamMember(JSON.toJSONString(memberIds));
        dataCommon.setUpdateTime(DateUtil.date());
        dataCommonService.updateById(dataCommon);
        // 更新ES
        Map<String, Object> fieldValueMap = new HashMap<>(1);
        fieldValueMap.put("teamMember", memberIds);
        updateField(fieldValueMap, dataCommon.getDataId(), dataCommon.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTeamMember(TeamMemberRemoveBO memberRemoveBO) {
        if (CollUtil.isEmpty(memberRemoveBO.getMemberIds())) {
            return;
        }
        if (CollUtil.isEmpty(memberRemoveBO.getMemberIds())) {
            return;
        }
        if (ObjectUtil.isNull(memberRemoveBO.getModuleId())) {
            throw new BusinessException(ModuleCodeEnum.MODULE_ID_IS_NULL_ERROR);
        }
        if (ObjectUtil.isNull(memberRemoveBO.getDataId())) {
            throw new BusinessException(ModuleCodeEnum.DATA_ID_IS_NULL);
        }
        ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(memberRemoveBO.getDataId());
        if (memberRemoveBO.getMemberIds().contains(dataCommon.getOwnerUserId())) {
            throw new BusinessException(ModuleCodeEnum.TEAM_OWNER_CAN_NOT_BE_REMOVED);
        }
        ModuleEntity module = moduleService.getByModuleIdAndVersion(memberRemoveBO.getModuleId(), dataCommon.getVersion());
        // 获取模块主字段值
        String mainFieldValue = fieldDataProvider.queryValue(memberRemoveBO.getDataId(), module.getMainFieldId());
        lambdaUpdate()
                .eq(ModuleTeamMember::getModuleId, memberRemoveBO.getModuleId())
                .eq(ModuleTeamMember::getDataId, memberRemoveBO.getDataId())
                .in(ModuleTeamMember::getUserId, memberRemoveBO.getMemberIds())
                .remove();
        List<ModuleDataOperationRecord> operationRecords = new ArrayList<>();
        for (Long memberId : memberRemoveBO.getMemberIds()) {
            operationRecords.add(operationRecordService.initTeamUserEntity(module.getModuleId(), module.getVersion(),
                    memberRemoveBO.getDataId(), mainFieldValue, memberId, ActionTypeEnum.REMOVE_TEAM_MEMBER));
        }
        MessageBO messageBO = new MessageBO();
        messageBO.setDataId(memberRemoveBO.getDataId());
        String msgValue = "";
        if (ObjectUtil.equal(1, memberRemoveBO.getMemberIds().size()) && ObjectUtil.equal(UserUtil.getUserId(), memberRemoveBO.getMemberIds().get(0))) {
            // 您已退出" + module.getName() + "《" + mainFieldValue + "》" + "的团队"
            msgValue = "您已退出" + module.getName();
        } else {
            // 您已被"+ UserUtil.getUser().getRealname() + "移出" + module.getName() + "《" + mainFieldValue + "》" + "的团队
            msgValue = "您已被"+ UserUtil.getUser().getNickname() + "移出" + module.getName();
        }
        messageBO.setValue(msgValue);
        messageBO.setModuleId(module.getModuleId());
        messageBO.setModuleName(module.getName());
        messageBO.setTypeId(0L);
        messageBO.setTypeName(mainFieldValue);
        messageBO.setExtData(null);
        messageBO.setType(4);
        messageBO.setReceivers(memberRemoveBO.getMemberIds());
        messageBO.setCreateUserId(UserUtil.getUserId());
        ApplicationContextHolder.getBean(IMessageService.class).sendMessage(messageBO);
        operationRecordService.saveBatch(operationRecords);
        // 更新团队成员到 dataCommon
        List<ModuleTeamMember> teamMembers = this.getTeamMemberByDataId(dataCommon.getModuleId(), dataCommon.getDataId());
        List<Long> memberIds = teamMembers.stream().map(ModuleTeamMember::getUserId).collect(Collectors.toList());
        dataCommon.setTeamMember(JSON.toJSONString(memberIds));
        dataCommon.setUpdateTime(DateUtil.date());
        dataCommonService.updateById(dataCommon);
        // 更新ES
        Map<String, Object> fieldValueMap = new HashMap<>(1);
        fieldValueMap.put("teamMember", memberIds);
        updateField(fieldValueMap, dataCommon.getDataId(), dataCommon.getModuleId());
    }

    @Override
    public List<TeamMemberVO> getTeamMember(Long moduleId, Long dataId) {
        List<TeamMemberVO> result = new ArrayList<>();
        ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(dataId);
        List<ModuleTeamMember> teamMembers = lambdaQuery().eq(ModuleTeamMember::getModuleId, moduleId).eq(ModuleTeamMember::getDataId, dataId).list();
        for (ModuleTeamMember teamMember : teamMembers) {
            // 如果团队成员中有负责人，则删除这个负责人
            if (ObjectUtil.equal(dataCommon.getOwnerUserId(), teamMember.getUserId())) {
                removeById(teamMember.getId());
            }
            TeamMemberVO vo = new TeamMemberVO();
            SimpleUser userInfo = UserCacheUtil.getSimpleUser(teamMember.getUserId());
            vo.setRealname(userInfo.getNickname());
            vo.setDeptName(userInfo.getDeptName());
            vo.setPower(teamMember.getPower());
            vo.setUserId(teamMember.getUserId());
            vo.setCreateTime(teamMember.getCreateTime());
            vo.setExpiresTime(teamMember.getExpiresTime());
            vo.setCreateUserId(teamMember.getCreateUserId());
            result.add(vo);
        }
        // 负责人
        TeamMemberVO vo = new TeamMemberVO();
        SimpleUser userInfo = UserCacheUtil.getSimpleUser(dataCommon.getOwnerUserId());
        vo.setRealname(userInfo.getNickname());
        vo.setDeptName(userInfo.getDeptName());
        vo.setPower(3);
        vo.setUserId(dataCommon.getOwnerUserId());
        result.add(vo);
        return result;
    }

    @Override
    public TeamMemberVO getTeamMemberInfo(Long moduleId, Long dataId) {
        ModuleTeamMember teamMember = lambdaQuery().eq(ModuleTeamMember::getModuleId, moduleId)
                .eq(ModuleTeamMember::getDataId, dataId)
                .eq(ModuleTeamMember::getUserId, UserUtil.getUserId())
                .one();
        if (ObjectUtil.isNull(teamMember)) {
            return null;
        }
        TeamMemberVO vo = new TeamMemberVO();
        SimpleUser userInfo = UserCacheUtil.getSimpleUser(teamMember.getUserId());
        vo.setRealname(userInfo.getNickname());
        vo.setDeptName(userInfo.getDeptName());
        vo.setPower(teamMember.getPower());
        vo.setUserId(teamMember.getUserId());
        vo.setCreateTime(teamMember.getCreateTime());
        vo.setExpiresTime(teamMember.getExpiresTime());
        vo.setCreateUserId(teamMember.getCreateUserId());
        return vo;
    }

    @Override
    public List<ModuleTeamMember> getTeamMemberByDataId(Long moduleId, Long dataId) {
        return lambdaQuery().eq(ModuleTeamMember::getModuleId, moduleId).eq(ModuleTeamMember::getDataId, dataId).list();
    }
}
