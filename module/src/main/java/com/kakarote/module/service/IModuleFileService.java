package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModuleFile;

public interface IModuleFileService extends BaseService<ModuleFile> {

    void saveModuleFile(ModuleFile moduleFile);

    ModuleFile queryModuleFile(ModuleFile moduleFile);
}
