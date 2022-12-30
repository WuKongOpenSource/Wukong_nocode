package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.VO.ModuleGroupVO;
import com.kakarote.module.service.IModuleGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwl
 * @date 20220305
 */
@RestController
@RequestMapping("/moduleGroup")
@Api(tags = "模块分组")
public class ModuleGroupController {
    @Resource
    private IModuleGroupService moduleGroupService;

    @PostMapping("/list/{applicationId}")
    @ApiOperation("查询分组")
    public Result<List<ModuleGroupVO>> getGroupList(@PathVariable("applicationId") Long applicationId){
        return Result.ok(moduleGroupService.getGroupList(applicationId));
    }
}
