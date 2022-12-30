package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.ModuleFieldUnionSaveBO;
import com.kakarote.module.service.IModuleFieldUnionProvider;
import com.kakarote.module.service.IModuleFieldUnionService;
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
 * @date: 2021-05-08 14:42
 */
@RestController
@RequestMapping("/moduleFieldUnion")
@Api(tags = "数据关联")
public class ModuleFieldUnionController {

	@Autowired
	private IModuleFieldUnionProvider fieldUnionProvider;

	@ApiOperation("查询数据关联(多选)字段的配置")
	@PostMapping("/query/{moduleId}/{version}")
	public Result<List<ModuleFieldUnionSaveBO>> query(@PathVariable("moduleId") Long moduleId, @PathVariable("version") Integer version) {
		List<ModuleFieldUnionSaveBO> result = fieldUnionProvider.query(moduleId, version);
		return Result.ok(result);
	}
}
