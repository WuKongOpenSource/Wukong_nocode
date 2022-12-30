package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.StageTask;
import com.kakarote.module.mapper.StageTaskMapper;
import com.kakarote.module.service.IStageTaskService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zjj
 * @title: StageTaskServiceImpl
 * @description: 阶段流程服务实现
 * @date 2022/4/12 11:13
 */
@Service
public class StageTaskServiceImpl extends BaseServiceImpl<StageTaskMapper, StageTask> implements IStageTaskService {

    @Override
    public List<StageTask> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery().eq(StageTask::getModuleId, moduleId).eq(StageTask::getVersion, version).list();
    }
}
