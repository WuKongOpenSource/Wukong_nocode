package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.RouterBO;
import com.kakarote.module.service.IRouterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 全局路由配置表 前端控制器
 *
 * @author zjj
 * @since 2022-08-08
 */
@RestController
@RequestMapping("/router")
@Api(tags = "路由配置")
public class RouterController {

    @Autowired
    private IRouterService routerService;

    @PostMapping("/list/{applicationId}")
    @ApiOperation("查询应用的路由信息")
    public Result<List<RouterBO>> list(@PathVariable Long applicationId) {
        List<RouterBO> routerBOS = routerService.listRouter(applicationId);
        return Result.ok(routerBOS);
    }
}
