package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleGroupModuleSortBO;
import com.kakarote.module.entity.PO.ModuleGroupSort;
import com.kakarote.module.entity.VO.ModuleGroupSortVO;

import java.util.List;

/**
 * @author wwl
 * @date 2022/3/24 19:40
 */

public interface IModuleGroupSortService extends BaseService<ModuleGroupSort> {

    /**
     * 保存分组关系并排序
     *
     * @param sortBO 参数
     */
    ModuleGroupSort save(ModuleGroupModuleSortBO sortBO);

    /**
     * 配置段 查询list
     *
     * @param applicationId 应用id
     * @return data
     */
    List<ModuleGroupSortVO> queryList(Long applicationId);

    /**
     * 获取应用的分组排序
     *
     * @param applicationId
     * @return
     */
    List<ModuleGroupSort> getByApplicationId(Long applicationId);
}
