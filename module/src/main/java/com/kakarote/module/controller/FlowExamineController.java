package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.ExamineBO;
import com.kakarote.module.entity.PO.FlowExamineRecord;
import com.kakarote.module.service.IAuditExamineProvider;
import com.kakarote.module.service.IFlowExamineProvider;
import com.kakarote.module.service.IFlowExamineRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/moduleFlowExamine")
@Api(tags = "流程配置")
public class FlowExamineController {

    @Autowired
    private IFlowExamineRecordService examineRecordService;

    @Autowired
    private IAuditExamineProvider examineProvider;

    @PostMapping("/auditExamine")
    @ApiOperation("审批")
    public Result auditExamine(@RequestBody ExamineBO examineBO) {
        examineProvider.auditExamine(examineBO);
        return Result.ok();
    }

    @PostMapping("/record/{moduleId}/{dataId}/{typeId}")
    @ApiOperation("审核记录")
    public Result<FlowExamineRecord> getRecord(@PathVariable("moduleId") Long moduleId, @PathVariable("dataId") Long dataId, @PathVariable("typeId") Long typeId) {
        FlowExamineRecord record = examineRecordService.getRecordByModuleIdAndDataId(moduleId, dataId, typeId);
        return Result.ok(record);
    }
}
