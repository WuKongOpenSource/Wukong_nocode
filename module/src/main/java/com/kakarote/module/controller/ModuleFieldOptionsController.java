package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.PO.ModuleFieldOptions;
import com.kakarote.module.service.IModuleFieldOptionsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-07-17 20:46
 */
@RestController
@RequestMapping("/moduleFieldOptions")
@Api(tags = "自定义字段选项模块")
public class ModuleFieldOptionsController {

	@Autowired
	private IModuleFieldOptionsService fieldOptionsService;

	@ApiOperation("查询字段选项")
	@PostMapping("/list/{moduleId}/{version}")
	public Result<List<ModuleFieldOptions>> queryDefaultValueList(@PathVariable("moduleId") Long moduleId, @PathVariable("version") Integer version) {
		List<ModuleFieldOptions> fieldOptions = fieldOptionsService.getByModuleIdAndVersion(moduleId, version);
		return Result.ok(fieldOptions);
	}
}
