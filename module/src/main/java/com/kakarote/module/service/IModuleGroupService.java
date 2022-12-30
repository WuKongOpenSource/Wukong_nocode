package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModuleGroup;
import com.kakarote.module.entity.VO.ModuleGroupVO;

import java.util.List;

/**
 * 模块分组
 *
 * @author wwl
 * @date 20220304
 */
public interface IModuleGroupService extends BaseService<ModuleGroup> {

    /**
     * 查询 分组列表
     *
     * @param applicationId 应用id
     * @return 分组list
     */
    List<ModuleGroupVO> getGroupList(Long applicationId);

    /**
     * 获取应用的分组
     *
     * @param applicationId
     * @return
     */
    List<ModuleGroup> getByApplicationId(Long applicationId);

    /**
     * 删除应用下分组
     *
     * @param applicationId 应用id
     */
    void deleteByApplicationId(Long applicationId);
}
