package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.StageTask;

import java.util.List;

/**
 * @author zjj
 * @title: IStageTaskService
 * @description: 阶段任务服务接口
 * @date 2022/4/12 11:12
 */
public interface IStageTaskService extends BaseService<StageTask> {

    /**
     * 获取模块的阶段任务
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<StageTask> getByModuleIdAndVersion(Long moduleId, Integer version);
}
