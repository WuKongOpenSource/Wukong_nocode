package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleDataCheckRequestBO;
import com.kakarote.module.entity.PO.ModuleDataCheckRule;
import com.kakarote.module.entity.VO.ModuleDataCheckResultVO;

import java.util.List;

/**
 * @author zjj
 * @title: IModuleDataCheckService
 * @description: 数据校验服务接口
 * @date 2022/3/26 14:14
 */

public interface IModuleDataCheckRuleService extends BaseService<ModuleDataCheckRule> {

    /**
     * 根据模块id和版本号获取数据校验配置
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<ModuleDataCheckRule> getByModuleIdAndVersion(Long moduleId, Integer version);

    /**
     *  数据校验
     *
     * @param requestBO
     * @return
     */
    List<ModuleDataCheckResultVO> dataCheck(ModuleDataCheckRequestBO requestBO);

}
