package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.AppCategory;
import com.kakarote.module.service.IAppCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zjj
 * @description: AppCategoryController
 * @date 2022/6/16
 */
@RestController
@RequestMapping("/moduleAppCategory")
@Api(tags = "应用分类关系管理")
public class AppCategoryController {

    @Autowired
    private IAppCategoryService appCategoryService;


    @PostMapping("/app/store")
    @ApiOperation("应用收藏")
    public Result storeApp(@RequestBody AppStoreBO storeBO) {
        appCategoryService.storeApp(storeBO);
        return Result.ok();
    }

    @PostMapping("/saveAppCustomCategory")
    @ApiOperation("添加应用到自定义分类")
    public Result saveAppCustomCategory(@RequestBody AppCategorySaveBO moveBO) {
        appCategoryService.saveAppCustomCategory(moveBO);
        return Result.ok();
    }

    @PostMapping("/app/list")
    @ApiOperation("获取非系统应用分类关系")
    public Result<List<AppCategoryBO>> list() {
        List<AppCategoryBO> categoryBOS = appCategoryService.getAll();
        return Result.ok(categoryBOS);
    }
}
