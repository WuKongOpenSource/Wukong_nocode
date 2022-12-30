package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.Stage;
import com.kakarote.module.mapper.StageMapper;
import com.kakarote.module.service.IStageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zjj
 * @title: StageServiceImpl
 * @description: 阶段服务实现
 * @date 2022/4/11 15:52
 */
@Service
public class StageServiceImpl extends BaseServiceImpl<StageMapper, Stage> implements IStageService {

    @Override
    public List<Stage> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery().eq(Stage::getModuleId, moduleId).eq(Stage::getVersion, version).list();
    }

    @Override
    public Stage getByModuleIdAndVersion(Long moduleId, Long stageId, Integer version) {
        return lambdaQuery()
                .eq(Stage::getModuleId, moduleId)
                .eq(Stage::getStageId, stageId)
                .eq(Stage::getVersion, version)
                .one();
    }
}
