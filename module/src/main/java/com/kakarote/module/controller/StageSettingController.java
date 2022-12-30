package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.StageSettingSaveBO;
import com.kakarote.module.service.IStageSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zjj
 * @title: StageFlowController
 * @description: 阶段流程
 * @date 2022/4/11 14:52
 */
@RestController
@RequestMapping("/moduleStageSetting")
@Api(tags = "阶段流程")
public class StageSettingController {

    @Autowired
    private IStageSettingService stageSettingService;


    @ApiOperation("数据详情页获取阶段流程配置")
    @PostMapping("/queryList/{dataId}")
    public Result<List<StageSettingSaveBO>> queryListByDataId(@PathVariable("dataId") Long dataId) {
        List<StageSettingSaveBO> result = stageSettingService.queryList(dataId);
        return Result.ok(result);
    }
}
