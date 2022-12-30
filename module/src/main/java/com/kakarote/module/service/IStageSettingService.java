package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.StageSettingSaveBO;
import com.kakarote.module.entity.PO.StageSetting;

import java.util.List;

/**
 * @author zjj
 * @title: IStageSettingService
 * @description: 阶段流程服务接口
 * @date 2022/4/11 14:20
 */
public interface IStageSettingService extends BaseService<StageSetting> {

    /**
     *  获取模块的阶段流程
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<StageSetting> getByModuleIdAndVersion(Long moduleId, Integer version);

    /**
     * 获取阶段流程配置
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<StageSettingSaveBO> queryList(Long moduleId, Integer version);

    /**
     * 获取阶段流程配置
     *
     * @param dataId
     * @return
     */
    List<StageSettingSaveBO> queryList(Long dataId);
}
