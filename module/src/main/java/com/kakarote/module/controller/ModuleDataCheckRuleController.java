package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.ModuleDataCheckRequestBO;
import com.kakarote.module.entity.VO.ModuleDataCheckResultVO;
import com.kakarote.module.service.IModuleDataCheckRuleService;
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
 * @title: ModuleDataCheckRuleController
 * @description: 数据校验规则
 * @date 2022/3/26 15:07
 */
@RestController
@RequestMapping("/moduleDataCheckRule")
@Api(tags = "数据校验规则")
public class ModuleDataCheckRuleController {

    @Autowired
    private IModuleDataCheckRuleService checkRuleService;

    @ApiOperation("数据校验")
    @PostMapping("/dataCheck")
    public Result<List<ModuleDataCheckResultVO>> dataCheck(@RequestBody ModuleDataCheckRequestBO requestBO) {
        List<ModuleDataCheckResultVO> result = checkRuleService.dataCheck(requestBO);
        return Result.ok(result);
    }

}
