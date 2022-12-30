package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.entity.BO.ModuleGroupModuleSortBO;
import com.kakarote.module.entity.PO.ModuleGroup;
import com.kakarote.module.entity.PO.ModuleGroupSort;
import com.kakarote.module.entity.VO.ModuleGroupSortVO;
import com.kakarote.module.entity.VO.ModuleListVO;
import com.kakarote.module.mapper.ModuleGroupSortMapper;
import com.kakarote.module.service.IModuleGroupService;
import com.kakarote.module.service.IModuleGroupSortService;
import com.kakarote.module.service.IModuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wwl
 * @date 2022/3/24 19:41
 */
@Service
public class ModuleGroupSortServiceImpl extends BaseServiceImpl<ModuleGroupSortMapper, ModuleGroupSort> implements IModuleGroupSortService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModuleGroupSort save(ModuleGroupModuleSortBO sortBO) {
        boolean flag = ObjectUtil.isNotNull(sortBO.getGroupId()) && ObjectUtil.isNotNull(sortBO.getModuleId());
        ModuleGroupSort sortEntity;
        int sort = 0;
        if (ObjectUtil.isNotNull(sortBO.getModuleId())) {
            ModuleGroupSort one = this.lambdaQuery()
                    .eq(ModuleGroupSort::getApplicationId, sortBO.getApplicationId())
                    .eq(ModuleGroupSort::getModuleId, sortBO.getModuleId())
                    .one();
            if (ObjectUtil.isNotNull(one)) {
                if (ObjectUtil.equal(one.getGroupId(), sortBO.getGroupId())) {
                    return one;
                } else {
                    this.lambdaUpdate()
                            .eq(ModuleGroupSort::getApplicationId, sortBO.getApplicationId())
                            .eq(ModuleGroupSort::getModuleId, sortBO.getModuleId())
                            .remove();
                }
            }
        }
        if (flag) {
            sortEntity = this.getBaseMapper().getMaxSortInGroup(sortBO.getApplicationId(), sortBO.getGroupId());
        } else {
            sortEntity = this.getBaseMapper().getMaxSortInApplication(sortBO.getApplicationId());
        }
        if (ObjectUtil.isNotNull(sortEntity)) {
            sort = sortEntity.getSort();
        }
        ModuleGroupSort moduleSort = new ModuleGroupSort();
        moduleSort.setGroupId(sortBO.getGroupId());
        moduleSort.setModuleId(sortBO.getModuleId());
        moduleSort.setSort(sort + 1);
        moduleSort.setApplicationId(sortBO.getApplicationId());
        moduleSort.setCreateTime(LocalDateTimeUtil.now());
        moduleSort.setCreateUserId(UserUtil.getUserId());
        this.saveOrUpdate(moduleSort);
        return moduleSort;
    }


    @Override
    public List<ModuleGroupSortVO> queryList(Long applicationId) {
        List<ModuleGroupSortVO> outterList = this.getBaseMapper().getOutterList(applicationId);
        LinkedList<ModuleGroupSortVO> result = new LinkedList<>();
        if (CollUtil.isNotEmpty(outterList)) {
            // 所有状态的module
            List<ModuleListVO> moduleListVOS = ApplicationContextHolder.getBean(IModuleService.class).queryModuleList(applicationId);
            for (ModuleGroupSortVO sortVO : outterList) {
                // 分组
                if (ObjectUtil.isNotNull(sortVO.getGroupId())) {
                    ModuleGroup group = ApplicationContextHolder
                            .getBean(IModuleGroupService.class)
                            .lambdaQuery()
                            .eq(ModuleGroup::getApplicationId, applicationId)
                            .eq(ModuleGroup::getId, sortVO.getGroupId())
                            .one();
                    sortVO.setIcon(group.getIcon());
                    sortVO.setIconColor(group.getIconColor());
                    sortVO.setName(group.getGroupName());
                    sortVO.setCreateTime(Date.from(group.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
                    sortVO.setCreateUserId(group.getCreateUserId());
                    sortVO.setUpdateTime(ObjectUtil.isNotNull(group.getUpdateTime()) ? Date.from(group.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant()) : null);
                    sortVO.setCreateUserName(UserCacheUtil.getUserName(group.getCreateUserId()));
                    List<ModuleGroupSortVO> innerList = this.getBaseMapper().getInnerModule(sortVO.getGroupId());
                    List<ModuleListVO> innerModuleList = new LinkedList<>();
                    for (ModuleGroupSortVO innerModule : innerList) {
                        moduleListVOS
                                .stream()
                                .filter(f -> ObjectUtil.equal(innerModule.getModuleId(), f.getModuleId()))
                                .peek(module -> {
                                    if (ObjectUtil.equal(innerModule.getModuleId(), module.getModuleId())) {
                                        module.setSort(innerModule.getSort());
                                    }
                                    innerModuleList.add(module);
                                }).collect(Collectors.toList());
                    }
                    sortVO.setChildList(innerModuleList);
                    result.add(sortVO);
                }
                // 模块
                else {
                    Integer sort = sortVO.getSort();
                    moduleListVOS.stream()
                            .filter(module -> ObjectUtil.equal(sortVO.getModuleId(), module.getModuleId()))
                            .map(module1 -> {
                                ModuleGroupSortVO moduleGroupSortVO = BeanUtil.copyProperties(module1, ModuleGroupSortVO.class);
                                moduleGroupSortVO.setSort(sort);
                                result.add(moduleGroupSortVO);
                                return moduleGroupSortVO;
                            }).collect(Collectors.toList());
                }
            }
        }
        return result;
    }

    @Override
    public List<ModuleGroupSort> getByApplicationId(Long applicationId) {
        return lambdaQuery().eq(ModuleGroupSort::getApplicationId, applicationId).list();
    }
}
