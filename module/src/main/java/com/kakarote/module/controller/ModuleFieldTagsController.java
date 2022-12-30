package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.PO.ModuleFieldTags;
import com.kakarote.module.service.IModuleFieldTagsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wwl
 */
@RestController
@RequestMapping("/moduleFieldTags")
@Api(tags = "自定义标签字段选项模块")
public class ModuleFieldTagsController {

    @Autowired
    private IModuleFieldTagsService fieldTagsService;

    @ApiOperation("查询自定义标签字段选项")
    @PostMapping("/list/{moduleId}/{version}")
    public Result<List<ModuleFieldTags>> queryDefaultValueList(@PathVariable("moduleId") Long moduleId, @PathVariable("version") Integer version) {
        List<ModuleFieldTags> tags = fieldTagsService.getByModuleIdAndVersion(moduleId, version);
        return Result.ok(tags);
    }

}
