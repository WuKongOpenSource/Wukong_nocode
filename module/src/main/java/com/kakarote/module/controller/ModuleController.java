package com.kakarote.module.controller;


import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.ModuleSaveBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleLayout;
import com.kakarote.module.entity.VO.ModuleListVO;
import com.kakarote.module.service.IModuleLayoutService;
import com.kakarote.module.service.IModuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 应用模块表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@RestController
@RequestMapping("/moduleM")
@Api(tags = "模块管理")
public class ModuleController {

    @Autowired
    private IModuleLayoutService layoutService;

    @Autowired
    private IModuleService moduleService;

    @PostMapping("/queryDetail/{moduleId}/{version}")
    @ApiOperation("查询模块详情")
    public Result<ModuleSaveBO> queryDetail(@PathVariable("moduleId") Long moduleId, @PathVariable("version") Integer version) {
        return Result.ok(moduleService.queryDetail(moduleId, version));
    }

    @PostMapping("/queryById/{moduleId}")
    @ApiOperation("查询模块详情")
    public Result<ModuleSaveBO> queryById(@PathVariable("moduleId") Long moduleId, @RequestParam("isLatest") Boolean isLatest) {
        return Result.ok(moduleService.queryById(moduleId, isLatest));
    }

    @PostMapping("/list/{applicationId}")
    @ApiOperation("查看模块列表")
    public Result<List<ModuleListVO>> queryModuleList(@PathVariable("applicationId") Long applicationId) {
        List<ModuleListVO> moduleList = moduleService.queryModuleList(applicationId);
        return Result.ok(moduleList);
    }

    @PostMapping("/latest/list/{applicationId}")
    @ApiOperation("获取最新的模块")
    public Result<List<ModuleEntity>> getLatestModules(@PathVariable("applicationId") Long applicationId) {
        List<ModuleEntity> moduleList = moduleService.getLatestModules(applicationId);
        return Result.ok(moduleList);
    }

    @PostMapping("/bi/list/{applicationId}")
    @ApiOperation("获取 BI 模块")
    public Result<List<ModuleEntity>> getBIModules(@PathVariable("applicationId") Long applicationId) {
        List<ModuleEntity> moduleList = moduleService.getBIModules(applicationId);
        return Result.ok(moduleList);
    }

    @PostMapping("/released/list/{applicationId}")
    @ApiOperation("获取应用下正在使用的模块")
    public Result<List<ModuleSaveBO>> getActiveModules(@PathVariable("applicationId") Long applicationId) {
        List<ModuleSaveBO> moduleList = moduleService.getActiveModules(applicationId);
        return Result.ok(moduleList);
    }

    @PostMapping("/delete/{moduleId}")
    @ApiOperation("删除模块")
    public Result delete(@PathVariable("moduleId") Long moduleId) {
        moduleService.deleteModule(Collections.singletonList(moduleId));
        return Result.ok();
    }

    @PostMapping("/pageLayout/{moduleId}/{version}")
    @ApiOperation("获取模块页面布局")
    public Result<ModuleLayout> getLayout(@PathVariable("moduleId") Long moduleId, @PathVariable("version") Integer version) {
        ModuleLayout layout = layoutService.getByModuleIdAndVersion(moduleId, version);
        return Result.ok(layout);
    }
}

