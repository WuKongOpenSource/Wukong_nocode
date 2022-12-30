package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.PO.ModuleFieldTree;
import com.kakarote.module.service.IModuleFieldTreeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : zjj
 * @since : 2022/12/1
 */
@RestController
@RequestMapping("/moduleFieldTree")
@Api(tags = "自定义树字段模块")
public class ModuleFieldTreeController {

    @Autowired
    private IModuleFieldTreeService fieldTreeService;

    @ApiOperation("查询自定义树字段配置")
    @PostMapping("/list/{moduleId}/{version}")
    public Result<List<ModuleFieldTree>> queryDefaultValueList(@PathVariable("moduleId") Long moduleId, @PathVariable("version") Integer version) {
        List<ModuleFieldTree> fieldTrees = fieldTreeService.getByModuleIdAndVersion(moduleId, version);
        return Result.ok(fieldTrees);
    }
}
