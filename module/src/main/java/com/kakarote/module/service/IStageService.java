package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.Stage;

import java.util.List;

/**
 * @author zjj
 * @title: IStageService
 * @description: 阶段服务接口
 * @date 2022/4/11 15:52
 */
public interface IStageService extends BaseService<Stage> {

    /**
     * 获取模块的阶段
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<Stage> getByModuleIdAndVersion(Long moduleId, Integer version);

    Stage getByModuleIdAndVersion(Long moduleId, Long stageId, Integer version);
}
