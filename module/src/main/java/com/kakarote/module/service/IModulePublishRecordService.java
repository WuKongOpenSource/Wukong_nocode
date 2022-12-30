package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModulePublishRecord;

import java.util.List;

/**
 * @author zjj
 * @description: IModulePublishRecordService
 * @date 2022/6/17
 */
public interface IModulePublishRecordService extends BaseService<ModulePublishRecord> {

    /**
     * 获取指定应用下模块最新的发布记录
     *
     * @param applicationId
     * @return
     */
    ModulePublishRecord getLatestByAppId(Long applicationId);

    /**
     * 获取每个应用下模块最新的发布记录
     *
     * @return
     */
    List<ModulePublishRecord> getLatestRecordGroupByAppId();
}
