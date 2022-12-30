package com.kakarote.module.controller;


import com.kakarote.common.result.Result;
import com.kakarote.module.entity.VO.ModuleDataOperationRecordVO;
import com.kakarote.module.service.IModuleDataOperationRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moduleOperationRecord")
@Api(tags = "模块字段值操作记录")
public class ModuleDataOperationRecordController {

    @Autowired
    private IModuleDataOperationRecordService operationRecordService;

    @ApiOperation("查询字段值的操作记录")
    @PostMapping("/query/{moduleId}/{dataId}")
    public Result<List<ModuleDataOperationRecordVO>> queryRecord(@PathVariable("moduleId") Long moduleId, @PathVariable("dataId") Long dataId) {
        List<ModuleDataOperationRecordVO> records = operationRecordService.queryRecord(moduleId, dataId);
        return Result.ok(records);
    }
}
