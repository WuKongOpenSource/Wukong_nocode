package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.ModulePublishRecord;
import com.kakarote.module.mapper.ModulePublishRecordMapper;
import com.kakarote.module.service.IModulePublishRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zjj
 * @description: ModulePublishRecordServiceImpl
 * @date 2022/6/17
 */
@Service
public class ModulePublishRecordServiceImpl extends BaseServiceImpl<ModulePublishRecordMapper, ModulePublishRecord> implements IModulePublishRecordService {

    @Override
    public ModulePublishRecord getLatestByAppId(Long applicationId) {
        return lambdaQuery()
                .eq(ModulePublishRecord::getApplicationId, applicationId)
                .orderByDesc(ModulePublishRecord::getCreateTime).one();
    }

    @Override
    public List<ModulePublishRecord> getLatestRecordGroupByAppId() {
        return getBaseMapper().getLatestRecordGroupByAppId();
    }
}
