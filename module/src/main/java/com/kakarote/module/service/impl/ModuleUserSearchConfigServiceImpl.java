package com.kakarote.module.service.impl;

import cn.hutool.core.date.DateUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.entity.PO.ModuleUserSearchConfig;
import com.kakarote.module.mapper.ModuleUserSearchConfigMapper;
import com.kakarote.module.service.IModuleUserSearchConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zjj
 * @title: ModuleUserSearchConfigServiceImpl
 * @description: 用戶搜索配置
 * @date 2021/11/2314:20
 */
@Service
public class ModuleUserSearchConfigServiceImpl extends BaseServiceImpl<ModuleUserSearchConfigMapper, ModuleUserSearchConfig>
        implements IModuleUserSearchConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(ModuleUserSearchConfig searchConfig) {
        lambdaUpdate().eq(ModuleUserSearchConfig::getModuleId, searchConfig.getModuleId())
                .eq(ModuleUserSearchConfig::getCreateUserId, UserUtil.getUserId())
                .remove();
        searchConfig.setCreateTime(DateUtil.date());
        save(searchConfig);
    }

    @Override
    public ModuleUserSearchConfig getByModuleIdAndUserId(Long moduleId) {
        return lambdaQuery()
                .eq(ModuleUserSearchConfig::getModuleId, moduleId)
                .eq(ModuleUserSearchConfig::getCreateUserId, UserUtil.getUserId()).one();
    }
}
