package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.entity.PO.ModuleGroup;
import com.kakarote.module.entity.PO.ModuleGroupSort;
import com.kakarote.module.entity.VO.ModuleGroupVO;
import com.kakarote.module.mapper.ModuleGroupMapper;
import com.kakarote.module.service.IModuleGroupService;
import com.kakarote.module.service.IModuleGroupSortService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模块分组
 * @author wwl
 * @date 20220304
 */
@Service
public class ModuleGroupServiceImpl  extends BaseServiceImpl<ModuleGroupMapper, ModuleGroup> implements IModuleGroupService {

    @Override
    public List<ModuleGroupVO>  getGroupList(Long applicationId) {
        List<ModuleGroupVO> resultList = baseMapper.getGroupList(applicationId);
        for (ModuleGroupVO groupVO : resultList) {
            groupVO.setCreateUserName(UserCacheUtil.getUserName(groupVO.getCreateUserId()));
        }
        return resultList;
    }

    @Override
    public List<ModuleGroup> getByApplicationId(Long applicationId) {
        return lambdaQuery().eq(ModuleGroup::getApplicationId, applicationId).list();
    }

    @Override
    public void deleteByApplicationId(Long applicationId) {
        // 删除所有分组
        this.lambdaUpdate()
                .eq(ModuleGroup::getApplicationId, applicationId)
                .remove();
        // 删除所有关系
        ApplicationContextHolder
                .getBean(IModuleGroupSortService.class)
                .lambdaUpdate()
                .eq(ModuleGroupSort::getApplicationId, applicationId)
                .remove();
    }

}
