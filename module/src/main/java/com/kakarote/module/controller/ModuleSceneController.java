package com.kakarote.module.controller;


import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.ModuleSceneConfigBO;
import com.kakarote.module.entity.VO.ModuleSceneVO;
import com.kakarote.module.service.IModuleSceneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 模块场景表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-03-22
 */
@RestController
@RequestMapping("/moduleScene")
@Api(tags = "模块场景查询")
public class ModuleSceneController {

    @Autowired
    private IModuleSceneService sceneService;

    @PostMapping("/list/{moduleId}")
    @ApiOperation("查询场景列表")
    public Result<List<ModuleSceneVO>> list(@ApiParam(name = "moduleId", value = "模块ID") @PathVariable("moduleId") Long moduleId) {
        return Result.ok(sceneService.queryList(moduleId));
    }

    @PostMapping("/add")
    @ApiOperation("新增场景")
    public Result add(@RequestBody ModuleSceneVO moduleSceneVO) {
        sceneService.saveScene(moduleSceneVO);
        return Result.ok();
    }

    @PostMapping("/update")
    @ApiOperation("修改场景")
    public Result update(@RequestBody ModuleSceneVO moduleSceneVO) {
        sceneService.updateScene(moduleSceneVO);
        return Result.ok();
    }

    @ApiOperation("删除场景")
    @PostMapping("/delete/{sceneId}")
    public Result deleteScene(@PathVariable Long sceneId) {
        sceneService.deleteScene(sceneId);
        return Result.ok();
    }

    @ApiOperation(" 设置默认场景")
    @PostMapping("/default/{sceneId}")
    public Result setDefault(@PathVariable Long sceneId) {
        sceneService.setDefault(sceneId);
        return Result.ok();
    }

    @ApiOperation(" 设置场景")
    @PostMapping("/config")
    public Result sceneConfig(@RequestBody ModuleSceneConfigBO sceneConfigBO) {
        sceneService.sceneConfig(sceneConfigBO);
        return Result.ok();
    }

    @ApiOperation("获取场景")
    @PostMapping("/{sceneId}")
    public Result<ModuleSceneVO> getBySceneId(@PathVariable Long sceneId) {
        ModuleSceneVO sceneVO = sceneService.getBySceneId(sceneId);
        return Result.ok(sceneVO);
    }
}

