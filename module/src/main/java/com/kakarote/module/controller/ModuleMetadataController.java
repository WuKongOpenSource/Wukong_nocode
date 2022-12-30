package com.kakarote.module.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.ModuleMetadataBO;
import com.kakarote.module.entity.PO.ModuleMetadata;
import com.kakarote.module.service.IModuleMetadataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 应用表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@RestController
@RequestMapping("/moduleMetadata")
@Api(tags = "应用管理")
public class ModuleMetadataController {

    @Autowired
    private IModuleMetadataService metadataService;

    @PostMapping("/update")
    @ApiOperation("修改应用")
    public Result update(@RequestBody @Validated ModuleMetadataBO moduleMetadataBO) {
        metadataService.update(moduleMetadataBO);
        return Result.ok();
    }

    @PostMapping("/queryById/{applicationId}")
    @ApiOperation("查询应用详情")
    public Result<ModuleMetadataBO> queryById(@PathVariable("applicationId") String applicationId) {
        ModuleMetadata metadata = metadataService.getById(applicationId);
        return Result.ok(BeanUtil.copyProperties(metadata, ModuleMetadataBO.class));
    }

    @PostMapping("/updateStatus/{applicationId}")
    @ApiOperation("修改应用状态")
    public Result updateStatus(@PathVariable("applicationId") Long applicationId,
                               @ApiParam(name = "status", value = "状态 0 停用 1 正常")
                               @RequestParam("status") Integer status) {
        metadataService.updateStatus(applicationId, status);
        return Result.ok();
    }

    @PostMapping("/customAppList")
    @ApiOperation("获取用户自定义应用列表")
    public Result<List<ModuleMetadata>> getCustomAppList() {
        List<ModuleMetadata> appList = metadataService.getCustomAppList();
        return Result.ok(appList);
    }

    @PostMapping("/delete/{applicationId}")
    @ApiOperation("删除应用")
    public Result delete(@PathVariable("applicationId") Long applicationId) {
        metadataService.delete(applicationId);
        return Result.ok();
    }

    @GetMapping("/export/{applicationId}")
    @ApiOperation("导出应用")
    public void export(@PathVariable("applicationId") Long applicationId, HttpServletResponse servletResponse) throws IOException {
        Map<String, Object> data = metadataService.queryAppConfig(applicationId);
        ModuleMetadata app = MapUtil.get(data, "app", ModuleMetadata.class);
        servletResponse.setContentType("application/octet-stream;charset=utf-8");
        String header = "attachment;filename=";
        String fileName = String.format("%s-%s.template", app.getName(), DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN));
        header = StrUtil.join("", header, fileName);
        header = URLEncodeUtil.encode(header, StandardCharsets.UTF_8);
        servletResponse.setHeader("Content-disposition", header);
        ServletOutputStream outputStream = servletResponse.getOutputStream();
        byte[] encode = Base64Utils.encode(JSON.toJSONBytes(data));
        IoUtil.write(outputStream, true, encode);
    }

    @PostMapping("/import")
    @ApiOperation("导入应用")
    public Result<ModuleMetadata> importApp(@RequestPart("file") MultipartFile file) throws IOException {
        ModuleMetadata metadata = metadataService.importApp(file);
        return Result.ok(metadata);
    }
}

