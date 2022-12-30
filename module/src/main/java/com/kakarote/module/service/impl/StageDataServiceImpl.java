package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.entity.BO.StageDataBO;
import com.kakarote.module.entity.BO.StageDataQueryBO;
import com.kakarote.module.entity.BO.StageDataSaveBO;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.entity.PO.Stage;
import com.kakarote.module.entity.PO.StageData;
import com.kakarote.module.mapper.StageDataMapper;
import com.kakarote.module.service.IModuleFieldDataCommonService;
import com.kakarote.module.service.IStageDataService;
import com.kakarote.module.service.IStageService;
import com.kakarote.module.service.ModulePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zjj
 * @title: StageDataServiceImpl
 * @description: 阶段数据服务实现
 * @date 2022/4/12 16:44
 */
@Service
public class StageDataServiceImpl extends BaseServiceImpl<StageDataMapper, StageData> implements IStageDataService, ModulePageService {

    @Autowired
    private IStageService stageService;

    @Autowired
    private IModuleFieldDataCommonService dataCommonService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveStageData(StageDataSaveBO saveBO) {
        Long dataId = saveBO.getDataId();
        ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(dataId);
        if(ObjectUtil.isNull(dataCommon)) {
            throw new BusinessException(ModuleCodeEnum.DATA_NOT_EXIST_OR_DELETE);
        }
        Long moduleId = saveBO.getModuleId();
        Integer version = dataCommon.getVersion();
        Long stageSettingId = saveBO.getStageSettingId();
        Long stageId = saveBO.getStageId();
        Stage stage = null;
        if (ObjectUtil.notEqual(0L, stageId)) {
            stage = stageService.getByModuleIdAndVersion(moduleId, stageId, version);
            if (ObjectUtil.isNull(stage)) {
                throw new BusinessException(ModuleCodeEnum.STAGE_NOT_FOUND);
            }
        }
        StageData data = BeanUtil.copyProperties(saveBO, StageData.class);
        data.setVersion(version);
        if (saveBO.getIsMain()) {
            data.setIsMain(saveBO.getIsMain());
            data.setStageId(0L);
        }
        data.setCreateTime(DateUtil.date());
        lambdaUpdate()
                .eq(StageData::getModuleId, moduleId)
                .eq(StageData::getVersion, version)
                .eq(StageData::getStageSettingId, stageSettingId)
                .and(i -> i.eq(StageData::getStageId, data.getStageId())
                        .or()
                        .gt(StageData::getSort, saveBO.getSort()))
                .eq(StageData::getDataId, dataId)
                .remove();
        if (saveBO.getClearAll()) {
            return;
        }
        save(data);
        dataCommon.setStageId(data.getStageId());
        dataCommon.setStageName(ObjectUtil.isNotNull(stage) ? stage.getStageName() : saveBO.getStageName());
        dataCommon.setStageStatus(data.getStatus());
        dataCommon.setUpdateTime(DateUtil.date());
        dataCommonService.updateById(dataCommon);
        // 更新ES
        Map<String, Object> fieldValueMap = new HashMap<>(1);
        fieldValueMap.put("stageId", stageId);
        fieldValueMap.put("stageName", ObjectUtil.isNotNull(stage) ? stage.getStageName() : saveBO.getStageName());
        fieldValueMap.put("stageStatus", data.getStatus());
        fieldValueMap.put("updateTime", DateUtil.formatDateTime(dataCommon.getUpdateTime()));
        updateField(fieldValueMap, dataId, moduleId);
    }

    @Override
    public List<StageDataBO> queryList(StageDataQueryBO queryBO) {
        ModuleFieldDataCommon fieldDataCommon = ApplicationContextHolder.getBean(IModuleFieldDataCommonService.class).getByDataId(queryBO.getDataId());
        if(ObjectUtil.isNull(fieldDataCommon)) {
            throw new BusinessException(ModuleCodeEnum.DATA_NOT_EXIST_OR_DELETE);
        }
        List<StageData> dataList = lambdaQuery()
                .eq(StageData::getModuleId, fieldDataCommon.getModuleId())
                .eq(StageData::getVersion, fieldDataCommon.getVersion())
                .eq(StageData::getDataId, fieldDataCommon.getDataId())
                .orderByAsc(StageData::getSort)
                .list();
        List<StageDataBO> result = new ArrayList<>();
        for (StageData data : dataList) {
            StageDataBO dataBO = BeanUtil.copyProperties(data, StageDataBO.class, "taskData");
            JSONArray taskData = JSON.parseArray(data.getTaskData());
            if (CollUtil.isNotEmpty(taskData)) {
                JSONArray tasks = new JSONArray();
                for (int i = 0; i < taskData.size(); i++) {
                    JSONObject object = taskData.getJSONObject(i);
                    if (ObjectUtil.isNotNull(object)) {
                        object.put("username", UserCacheUtil.getUserName(MapUtil.getLong(object, "userId")));
                    }
                    tasks.add(object);
                }
                dataBO.setTaskData(tasks);
            }
            result.add(dataBO);
        }
        return result;
    }
}
