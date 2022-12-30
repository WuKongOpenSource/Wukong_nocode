package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.TreeDataBO;
import com.kakarote.module.entity.BO.TreeDataQueryBO;
import com.kakarote.module.service.IModuleTreeDataProvider;
import com.kakarote.module.service.IModuleTreeDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author : zjj
 * @since : 2022/12/3
 */
@RestController
@RequestMapping("/moduleTreeData")
@Api(tags = "树数据模块")
public class ModuleTreeDataController {

    @Autowired
    private IModuleTreeDataProvider treeDataProvider;

    @ApiOperation("获取指定树字段的指定节点树型数据")
    @PostMapping("/queryTreeData")
    public Result<TreeDataBO> queryDefaultValueList(@RequestBody TreeDataQueryBO queryBO) {
        TreeDataBO result = treeDataProvider.queryTreeDataByDataId(queryBO);
        return Result.ok(result);
    }
}
