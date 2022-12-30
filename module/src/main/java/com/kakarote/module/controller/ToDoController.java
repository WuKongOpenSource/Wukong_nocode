package com.kakarote.module.controller;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.ToDoBO;
import com.kakarote.module.entity.BO.ToDoListQueryBO;
import com.kakarote.module.entity.BO.ToDoUpdateBO;
import com.kakarote.module.service.IToDoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/moduleToDo")
@Api(tags = "待办")
public class ToDoController {

    @Autowired
    private IToDoService toDoService;

    @ApiOperation("查询待办列表")
    @PostMapping("/queryList")
    public Result<BasePage<ToDoBO>> queryList(@RequestBody ToDoListQueryBO queryBO) {
        BasePage<ToDoBO> result = toDoService.queryList(queryBO);
        return Result.ok(result);
    }

    @ApiOperation("待办已读")
    @PostMapping("/view/{id}")
    public Result view(@PathVariable Long id) {
       toDoService.setViewed(id);
        return Result.ok();
    }

    @ApiOperation("待办已更新")
    @PostMapping("/update")
    public Result update(@RequestBody ToDoUpdateBO updateBO) {
        toDoService.update(updateBO);
        return Result.ok();
    }
}
