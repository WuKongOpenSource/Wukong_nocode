package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.StageComment;
import com.kakarote.module.service.IStageCommentService;
import com.kakarote.module.service.IStageDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zjj
 * @title: StageDataController
 * @description: 阶段数据
 * @date 2022/4/12 17:36
 */
@RestController
@RequestMapping("/moduleStageData")
@Api(tags = "阶段数据")
public class StageDataController {

    @Autowired
    private IStageDataService dataService;

    @Autowired
    private IStageCommentService commentService;

    @PostMapping("/save")
    @ApiOperation("保存阶段数据")
    public Result save(@RequestBody StageDataSaveBO saveBO) {
        dataService.saveStageData(saveBO);
        return Result.ok();
    }

    @PostMapping("/queryList")
    @ApiOperation("查询阶段流程数据")
    public Result<List<StageDataBO>> queryList(@RequestBody StageDataQueryBO queryBO) {
        List<StageDataBO> result = dataService.queryList(queryBO);
        return Result.ok(result);
    }

    @PostMapping("/comment/save")
    @ApiOperation("保存阶段评论")
    public Result<StageComment> save(@RequestBody StageCommentSaveBO saveBO) {
        StageComment result = commentService.saveStageComment(saveBO);
        return Result.ok(result);
    }

    @PostMapping("/comment/remove")
    @ApiOperation("删除评论")
    public Result save(@RequestBody StageCommentDeleteBO saveBO) {
        commentService.deleteStageComment(saveBO);
        return Result.ok();
    }

    @PostMapping("/comment/list/{dataId}")
    @ApiOperation("评论列表")
    public Result<List<StageCommentSaveBO>> listComment(@PathVariable("dataId") Long dataId) {
        List<StageCommentSaveBO> result = commentService.queryList(dataId);
        return Result.ok(result);
    }
}
