package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.StageDataBO;
import com.kakarote.module.entity.BO.StageDataQueryBO;
import com.kakarote.module.entity.BO.StageDataSaveBO;
import com.kakarote.module.entity.PO.StageData;

import java.util.List;

/**
 * @author zjj
 * @title: IStageDataService
 * @description: 阶段数据服务接口
 * @date 2022/4/12 16:43
 */
public interface IStageDataService extends BaseService<StageData> {

    /**
     * 保存数据
     *
     * @param saveBO
     */
    void saveStageData(StageDataSaveBO saveBO);

    /**
     * 查询阶段流程数据
     *
     * @param queryBO
     * @return
     */
    List<StageDataBO> queryList(StageDataQueryBO queryBO);

}
