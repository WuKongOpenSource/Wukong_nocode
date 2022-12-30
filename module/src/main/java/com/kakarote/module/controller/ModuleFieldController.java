package com.kakarote.module.controller;


import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.VO.ModuleFieldSortVO;
import com.kakarote.module.service.IModuleFieldService;
import com.kakarote.module.service.IModuleFieldSortService;
import com.kakarote.module.service.IModuleFieldUnionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 自定义字段表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@RestController
@RequestMapping("/moduleField")
@Api(tags = "自定义字段模块")
public class ModuleFieldController {

    @Autowired
    private IModuleFieldService moduleFieldService;

    @Autowired
    private IModuleFieldSortService fieldSortService;

    @Autowired
    private IModuleFieldUnionService fieldUnionService;

    @ApiOperation("查询字段列表")
    @PostMapping("/queryList")
    public Result<List<ModuleFieldBO>> queryList(@RequestBody FieldQueryBO queryBO) {
        List<ModuleFieldBO> crmFieldList = moduleFieldService.queryList(queryBO);
        return Result.ok(crmFieldList);
    }

    @ApiOperation("查询字段列表(包含明细表格字段type==45)")
    @PostMapping("/queryExportHeadList")
    public Result<List<ModuleFieldSortVO>> queryExportHeadList(@RequestBody FieldQueryBO queryBO) {
        List<ModuleFieldSortVO> crmFieldList = moduleFieldService.queryExportHeadList(queryBO);
        return Result.ok(crmFieldList);
    }

	@ApiOperation("查询字段列表(二维数组)")
	@PostMapping("/formList")
	public Result<List<List<ModuleField>>> formList(@RequestBody FieldFormQueryBO queryBO) {
		List<List<ModuleField>> formList = moduleFieldService.formList(queryBO);
		return Result.ok(formList);
	}

    @ApiOperation("查询模块列表字段头部信息")
    @PostMapping("/queryListHead")
    public Result<List<ModuleFieldSortVO>> queryListHead(@RequestBody ListHeadQueryBO queryBO) {
        List<ModuleFieldSortVO> moduleFieldSortVOS = fieldSortService.queryListHead(queryBO);
        return Result.ok(moduleFieldSortVOS);
    }

    @ApiOperation("验证字段是否存在")
    @PostMapping("/verify")
    public Result<ModuleFieldVerifyBO> verify(@RequestBody ModuleFieldVerifyBO fieldVerifyBO) {
        ModuleFieldVerifyBO verifyBO = moduleFieldService.verify(fieldVerifyBO);
        return Result.ok(verifyBO);
    }

    @ApiOperation("设置表头的隐藏和显示")
    @PostMapping("/setFieldSort")
    public Result setFieldSort(@RequestBody ModuleFieldSortBO fieldSortBO) {
        fieldSortService.setFieldSort(fieldSortBO);
        return Result.ok();
    }

    @ApiOperation("字段样式设置")
    @PostMapping("/setFieldStyle")
    public Result setFieldStyle(@RequestBody ModuleFieldStyleSaveBO fieldStyleSaveBO) {
        fieldSortService.setFieldStyle(fieldStyleSaveBO);
        return Result.ok();
    }

    @ApiOperation("查询数据关联字段信息")
    @PostMapping("/queryUnionList/{targetModuleId}/{moduleId}")
    public Result<List<ModuleField>> queryUnionList(@PathVariable("targetModuleId") Long targetModuleId, @PathVariable("moduleId") Long moduleId) {
        List<ModuleField> res = fieldUnionService.queryUnionList(targetModuleId, moduleId);
        return Result.ok(res);
    }
}

