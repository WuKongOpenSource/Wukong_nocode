package com.kakarote.module.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.entity.PO.ModuleFile;
import com.kakarote.module.mapper.ModuleFileMapper;
import com.kakarote.module.service.IModuleFileService;
import org.springframework.stereotype.Service;

import static com.kakarote.module.constant.ModuleCodeEnum.*;

@Service
public class ModuleFileServiceImpl extends BaseServiceImpl<ModuleFileMapper, ModuleFile> implements IModuleFileService {


    @Override
    public void saveModuleFile(ModuleFile moduleFile) {
        if (ObjectUtil.isNull(moduleFile.getDataId())) {
            throw new BusinessException(DATA_ID_IS_NULL);
        }
        if (ObjectUtil.isNull(moduleFile.getModuleId())) {
            throw new BusinessException(MODULE_ID_IS_NULL_ERROR);
        }
        if (StrUtil.isEmpty(moduleFile.getBatchId())) {
            throw new BusinessException(FILE_BATCH_ID_IS_NULL);
        }
        ModuleFile file = lambdaQuery().eq(ModuleFile::getModuleId, moduleFile.getModuleId())
                .eq(ModuleFile::getDataId, moduleFile.getDataId())
                .eq(ModuleFile::getBatchId, moduleFile.getBatchId())
                .one();
        if (ObjectUtil.isNotNull(file)) {
            return;
        }
        moduleFile.setCreateTime(DateUtil.date());
        moduleFile.setCreateUserId(UserUtil.getUserId());
        save(moduleFile);
    }

    @Override
    public ModuleFile queryModuleFile(ModuleFile moduleFile) {
        return lambdaQuery().eq(ModuleFile::getModuleId, moduleFile.getModuleId())
                .eq(ModuleFile::getDataId, moduleFile.getDataId())
                .one();
    }
}
