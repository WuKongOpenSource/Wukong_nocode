package com.kakarote.module.controller;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.VO.TeamMemberVO;
import com.kakarote.module.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-08 17:39
 */
@RestController
@RequestMapping("/moduleFieldData")
@Api(tags = "模块字段值")
public class ModuleFieldDataController {

	@Autowired
	private IModuleFieldUnionConditionService fieldUnionConditionService;

	@Autowired
	private IModuleFieldDataService fieldDataService;

	@Autowired
	private IModuleFieldDataProvider fieldDataProvider;

	@Autowired
	private IModuleTeamMemberService teamMemberService;

	@Autowired
	private IModuleFieldFormulaProvider fieldFormulaProvider;

	@ApiOperation("保存模块字段值")
	@PostMapping("/save")
	public Result save(@RequestBody ModuleFieldDataSaveBO fieldDataBO) {
		fieldDataProvider.save(fieldDataBO);
		return Result.ok();
	}

    @ApiOperation("计算公式字段字段值")
    @PostMapping("/calculateFormula")
    public Result<List<ModuleFieldData>> calculateFieldFormula(@RequestBody ModuleFieldDataSaveBO fieldDataBO) {
        List<ModuleFieldData> result = fieldDataService.calculateFieldFormula(fieldDataBO);
        return Result.ok(result);
    }

	@ApiOperation("删除模块字段值")
	@PostMapping("/delete")
	public Result delete(@RequestBody ModuleFieldDataDeleteBO dataDeleteBO) {
		fieldDataService.delete(dataDeleteBO);
		return Result.ok();
	}

	@ApiOperation("查询单条数据字段值")
	@PostMapping("/query/{dataId}/{replaceMask}")
	public Result<ModuleFieldDataResponseBO> queryById(@PathVariable("dataId") Long dataId, @PathVariable("replaceMask") Boolean replaceMask) {
		ModuleFieldDataResponseBO responseBO = fieldDataProvider.queryById(dataId, replaceMask);
		return Result.ok(responseBO);
	}

	@ApiOperation("获取主字段值")
	@PostMapping("/mainField/{dataId}")
	public Result<String> queryMainFieldValue(@PathVariable("dataId") Long dataId) {
		String response = fieldDataService.queryMainFieldValue(dataId);
		return Result.ok(response);
	}

	@ApiOperation("数据关联结果查询")
	@PostMapping("/dataUnion")
	public Result<BasePage<Map<String, Object>>> searchDataUnionFieldData(@RequestBody ConditionSearchRequest searchBO) {
		BasePage<Map<String, Object>> result = fieldUnionConditionService.searchDataUnionFieldData(searchBO);
		return Result.ok(result);
	}

	@ApiOperation("转移负责人")
	@PostMapping("/transfer")
	public Result transferOwner(@RequestBody TransferOwnerBO transferOwnerBO) {
		fieldDataService.transferOwner(transferOwnerBO);
		return Result.ok();
	}

	@ApiOperation("字段值验重")
	@PostMapping("/doubleCheck")
	public Result<Boolean> doubleCheck(@RequestBody DoubleCheckBO checkBO) {
		Boolean result = fieldDataService.doubleCheck(checkBO);
		return Result.ok(result);
	}

	@ApiOperation("保存团队成员")
	@PostMapping("/saveTeamMember")
	public Result saveTeamMember(@RequestBody TeamMemberSaveBO teamMemberSaveBO) {
		teamMemberService.saveTeamMember(teamMemberSaveBO);
		return Result.ok();
	}

	@ApiOperation("移除团队成员")
	@PostMapping("/removeTeamMember")
	public Result removeTeamMember(@RequestBody TeamMemberRemoveBO teamMemberRemoveBO) {
		teamMemberService.removeTeamMember(teamMemberRemoveBO);
		return Result.ok();
	}

	@ApiOperation("获取数据的团队成员")
	@PostMapping("/teamMember/{moduleId}/{dataId}")
	public Result<List<TeamMemberVO>> getTeamMember(@PathVariable Long moduleId, @PathVariable Long dataId) {
		List<TeamMemberVO> response = teamMemberService.getTeamMember(moduleId, dataId);
		return Result.ok(response);
	}

	@ApiOperation("获取当前用户的团队成员信息")
	@PostMapping("/teamMemberInfo/{moduleId}/{dataId}")
	public Result<TeamMemberVO> getTeamMemberInfo(@PathVariable Long moduleId, @PathVariable Long dataId) {
		TeamMemberVO response = teamMemberService.getTeamMemberInfo(moduleId, dataId);
		return Result.ok(response);
	}

	@ApiOperation("设置数据分组")
	@PostMapping("/setDataCategory")
	public Result setDataCategory(@RequestBody SetDataCategoryBO bo) {
		fieldDataService.setDataCategory(bo);
		return Result.ok();
	}

	@ApiOperation("更新单个模块计算公式字段值")
	@PostMapping("/formulaField/update/{moduleId}")
	public Result updateFormulaFieldValue(@PathVariable Long moduleId) {
		fieldFormulaProvider.updateFormulaFieldValueByModuleId(moduleId);
		return Result.ok();
	}
}
