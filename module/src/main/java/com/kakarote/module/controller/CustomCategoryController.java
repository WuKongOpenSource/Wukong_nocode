package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.PO.CustomCategory;
import com.kakarote.module.service.ICustomCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zjj
 * @title: CustomModuleCategoryController
 * @description: 自定义模块分类
 * @date 2022/3/30 9:45
 */
@RestController
@RequestMapping("/moduleCustomCategory")
@Api(tags = "自定义模块分类")
public class CustomCategoryController {

    @Autowired
    private ICustomCategoryService categoryService;

    @ApiOperation("获取所有分类")
    @PostMapping("/list")
    public Result<List<CustomCategory>> getAll() {
        List<CustomCategory> result = categoryService.getAll();
        return Result.ok(result);
    }
}
