package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.PO.ModuleUserSearchConfig;
import com.kakarote.module.service.IModuleUserSearchConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zjj
 * @title: ModuleUserSearchConfigController
 * @description: 用戶搜索配置
 * @date 2021/11/2314:26
 */
@RestController
@RequestMapping("/moduleUserSearch")
@Api(tags = "流程配置")
public class ModuleUserSearchConfigController {

    @Autowired
    private IModuleUserSearchConfigService searchConfigService;

    @ApiOperation("保存用户搜索配置")
    @PostMapping("/save")
    public Result saveConfig(@RequestBody ModuleUserSearchConfig searchConfig) {
        searchConfigService.saveConfig(searchConfig);
        return Result.ok();
    }

    @ApiOperation("获取用户的搜索配置")
    @PostMapping("/config/{moduleId}")
    public Result<ModuleUserSearchConfig> getByModuleIdAndUserId(@PathVariable("moduleId") Long moduleId) {
        ModuleUserSearchConfig searchConfig = searchConfigService.getByModuleIdAndUserId(moduleId);
        return Result.ok(searchConfig);
    }
}
