package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.ConditionSearchRequest;
import com.kakarote.module.entity.BO.ModuleDefaultValueBO;
import com.kakarote.module.entity.BO.ModuleFieldDataSaveBO;
import com.kakarote.module.entity.VO.ModuleDefaultValueVO;
import com.kakarote.module.entity.VO.ModuleFieldValueVO;
import com.kakarote.module.service.IModuleFieldDefaultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-07-17 18:49
 */
@RestController
@RequestMapping("/moduleFieldDefault")
@Api(tags = "自定义字段默认值模块")
public class ModuleFieldDefaultController {

	@Autowired
	private IModuleFieldDefaultService fieldDefaultService;

	@ApiOperation("查询字段默认值配置")
	@PostMapping("/list/{moduleId}/{version}")
	public Result<List<ModuleDefaultValueBO>> queryDefaultValueList(@PathVariable Long moduleId, @PathVariable Integer version) {
		List<ModuleDefaultValueBO> defaultValueBOS = fieldDefaultService.queryDefaultValueList(moduleId, version);
		return Result.ok(defaultValueBOS);
	}


	@ApiOperation("查询字段默认值-固定值")
	@PostMapping("/values/{moduleId}/{version}")
	public Result<List<ModuleDefaultValueVO>> values(@PathVariable Long moduleId, @PathVariable Integer version) {
		List<ModuleDefaultValueVO> valueVOS = fieldDefaultService.values(moduleId, version);
		return Result.ok(valueVOS);
	}

	@ApiOperation("查询字段默认值（自定义筛选")
	@PostMapping("/values")
	public Result<List<ModuleFieldValueVO>> values(@RequestBody ConditionSearchRequest searchBO) {
		List<ModuleFieldValueVO> valueVOS = fieldDefaultService.values(searchBO);
		return Result.ok(valueVOS);
	}

	@ApiOperation("查询字段默认值（公式）")
	@PostMapping("/values/formula")
	public Result<List<ModuleFieldValueVO>> values(@RequestBody ModuleFieldDataSaveBO searchBO) {
		List<ModuleFieldValueVO> valueVOS = fieldDefaultService.values(searchBO);
		return Result.ok(valueVOS);
	}
}
