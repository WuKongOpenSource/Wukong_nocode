package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.entity.BO.StageSettingSaveBO;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.entity.PO.Stage;
import com.kakarote.module.entity.PO.StageSetting;
import com.kakarote.module.entity.PO.StageTask;
import com.kakarote.module.mapper.StageSettingMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: StageSettingServiceImpl
 * @description: 阶段流程服务实现
 * @date 2022/4/11 14:21
 */
@Service
public class StageSettingServiceImpl extends BaseServiceImpl<StageSettingMapper, StageSetting> implements IStageSettingService {

    @Autowired
    private IStageService stageService;

    @Autowired
    private IStageTaskService taskService;

    @Override
    public List<StageSetting> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery().eq(StageSetting::getModuleId, moduleId).eq(StageSetting::getVersion, version).list();
    }

    @Override
    public List<StageSettingSaveBO> queryList(Long moduleId, Integer version) {
        List<StageSetting> stageSettingList = this.getByModuleIdAndVersion(moduleId, version);
        if (CollUtil.isEmpty(stageSettingList)) {
            return null;
        }
        List<StageSettingSaveBO> result = stageSettingList.stream().map(b -> BeanUtil.copyProperties(b, StageSettingSaveBO.class)).collect(Collectors.toList());
        List<Stage> stages = stageService.getByModuleIdAndVersion(moduleId, version);
        List<StageTask> stageTasks = taskService.getByModuleIdAndVersion(moduleId, version);
        Map<Long, List<StageSettingSaveBO.StageBO>> stageGroupByStageSettingId = stages.stream()
                .collect(Collectors.groupingBy(Stage::getStageSettingId,
                        Collectors.mapping(s -> BeanUtil.copyProperties(s, StageSettingSaveBO.StageBO.class), Collectors.toList())));
        Map<Long, List<StageSettingSaveBO.StageTaskBO>> stageTaskGroupByStageId = stageTasks.stream()
                .collect(Collectors.groupingBy(StageTask::getStageId,
                        Collectors.mapping(t -> BeanUtil.copyProperties(t, StageSettingSaveBO.StageTaskBO.class), Collectors.toList())));
        for (StageSettingSaveBO settingSaveBO : result) {
            settingSaveBO.setCreateUser(UserCacheUtil.getSimpleUser(settingSaveBO.getCreateUserId()));
            settingSaveBO.setUserList(UserCacheUtil.getSimpleUsers(settingSaveBO.getUserIds()));
            if (CollUtil.isNotEmpty(settingSaveBO.getDeptIds())) {
                List<JSONObject> deptList = new ArrayList<>();
                for (Long deptId : settingSaveBO.getDeptIds()) {
                    JSONObject dept = new JSONObject();
                    dept.fluentPut("deptId", deptId).fluentPut("deptName", UserCacheUtil.getDeptName(deptId));
                    deptList.add(dept);
                }
                settingSaveBO.setDeptList(deptList);
            }
            List<StageSettingSaveBO.StageBO> stageBOS = stageGroupByStageSettingId.get(settingSaveBO.getStageSettingId());
            if (CollUtil.isNotEmpty(stageBOS)) {
                for (StageSettingSaveBO.StageBO stageBO : stageBOS) {
                    List<StageSettingSaveBO.StageTaskBO> stageTaskBOS = stageTaskGroupByStageId.get(stageBO.getStageId());
                    stageBO.setTaskBOList(stageTaskBOS);
                }
                settingSaveBO.setStageBOList(stageBOS);
            }
        }
        return result;
    }

    @Override
    public List<StageSettingSaveBO> queryList(Long dataId) {
        ModuleFieldDataCommon fieldDataCommon = ApplicationContextHolder.getBean(IModuleFieldDataCommonService.class).getByDataId(dataId);
        if(ObjectUtil.isNull(fieldDataCommon)) {
            throw new BusinessException(ModuleCodeEnum.DATA_NOT_EXIST_OR_DELETE);
        }
        List<StageSettingSaveBO> stageSettingSaveBOS = this.queryList(fieldDataCommon.getModuleId(), fieldDataCommon.getVersion());
        return stageSettingSaveBOS;
    }
}
