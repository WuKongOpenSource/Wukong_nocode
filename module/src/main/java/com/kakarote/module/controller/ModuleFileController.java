package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.PO.ModuleFile;
import com.kakarote.module.service.IModuleFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/moduleFile")
@Api(tags = "模块文件")
public class ModuleFileController {

    @Autowired
    private IModuleFileService fileService;

    @PostMapping("/query")
    @ApiOperation("查询模块文件")
    public Result<ModuleFile> queryModuleFile(@RequestBody ModuleFile moduleFile) {
        return Result.ok(fileService.queryModuleFile(moduleFile));
    }

    @PostMapping("/save")
    @ApiOperation("保存模块文件")
    public Result save(@RequestBody ModuleFile moduleFile) {
        fileService.saveModuleFile(moduleFile);
        return Result.ok();
    }
}
