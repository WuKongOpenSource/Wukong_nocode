package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.ModuleGroupModuleSortBO;
import com.kakarote.module.entity.VO.ModuleGroupSortVO;
import com.kakarote.module.service.IModuleGroupSortService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwl
 * @date 2022/3/24 19:49
 */
@RestController
@RequestMapping("/moduleGroupModuleSort")
@Api(tags = "模块与分组组合排序")
public class ModuleGroupModuleSortController {

    @Resource
    private IModuleGroupSortService moduleGroupModuleSortService;

    @PostMapping("/add")
    @ApiOperation("添加模块分组排序")
    public Result add(@RequestBody ModuleGroupModuleSortBO sortBO) {
        moduleGroupModuleSortService.save(sortBO);
        return Result.ok();
    }

    @PostMapping("/list/{applicationId}")
    @ApiOperation("查看模块-分组sort列表")
    public Result<List<ModuleGroupSortVO>> queryList(@PathVariable("applicationId") Long applicationId) {
        List<ModuleGroupSortVO> list = moduleGroupModuleSortService.queryList(applicationId);
        return Result.ok(list);
    }
}
