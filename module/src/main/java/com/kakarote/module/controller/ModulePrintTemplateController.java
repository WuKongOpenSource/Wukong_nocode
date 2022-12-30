package com.kakarote.module.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.redis.Redis;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.Result;
import com.kakarote.module.constant.ModuleConst;
import com.kakarote.module.entity.PO.ModulePrintRecord;
import com.kakarote.module.entity.PO.ModulePrintTemplate;
import com.kakarote.module.service.IModulePrintTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.List;

/**
 * @author wwl
 * @date 2022/3/9 11:31
 */
@RestController
@RequestMapping("/modulePrintTemplate")
@Api(tags = "打印模板")
@Slf4j
public class ModulePrintTemplateController {

    @Autowired
    private Redis redis;

    @Autowired
    private IModulePrintTemplateService modulePrintTemplateService;

    @ApiOperation("查询打印模板列表")
    @PostMapping("/list/{moduleId}/{version}")
    public Result<BasePage<ModulePrintTemplate>> queryPrintTemplateList(@PathVariable("moduleId") Long moduleId
            , @PathVariable("version") Integer version
            , @RequestBody PageEntity pageBo) {
        BasePage<ModulePrintTemplate> printTemplateBasePage = modulePrintTemplateService.queryPrintTemplateList(moduleId, version, pageBo);
        return Result.ok(printTemplateBasePage);
    }

    @ApiOperation("打印")
    @PostMapping("/print")
    public Result<String> print(@RequestParam("templateId") Long templateId, @RequestParam("dataId") Long dataId) {
        String print = modulePrintTemplateService.print(templateId, dataId);
        return Result.ok(print);
    }

    @ApiOperation("预览")
    @PostMapping("/preview")
    public Result<String> preview(@RequestParam("content") String content, @RequestParam("type") String type) {
        String s = modulePrintTemplateService.preview(content, type);
        return Result.ok(s);
    }

    @ApiOperation("iframe")
    @RequestMapping("/previewPdf")
    public void preview(String key, HttpServletResponse response) {
        String object = redis.get(ModuleConst.CRM_PRINT_TEMPLATE_CACHE_KEY + key);
        if (StrUtil.isNotEmpty(object)) {
            JSONObject parse = JSON.parseObject(object);
            String path = parse.getString("pdf");
            if (FileUtil.exist(path)) {
                File file = FileUtil.file(path);
                BufferedInputStream in = null;
                ServletOutputStream out = null;
                try {
                    in = FileUtil.getInputStream(file);
                    response.setContentType("application/pdf");
                    IoUtil.copy(in,response.getOutputStream());
                } catch (Exception ex) {
                    log.error("导出错误",ex);
                } finally {
                    IoUtil.close(in);
                    IoUtil.close(out);
                }
                return;
            }
        }
        ServletUtil.write(response, Result.ok().toJSONString(), "text/plain");
    }

    @ApiOperation("下载文件")
    @RequestMapping("/download")
    public void down(@RequestParam("type") Integer type, @RequestParam("key") String key, HttpServletResponse response) {
        String object = redis.get(ModuleConst.CRM_PRINT_TEMPLATE_CACHE_KEY + key);
        if (StrUtil.isNotEmpty(object)) {
            JSONObject parse = JSON.parseObject(object);
            String path;
            if (type == 2) {
                path = parse.getString("word");
            } else {
                path = parse.getString("pdf");
            }
            if (FileUtil.exist(path)) {
                ServletUtil.write(response, FileUtil.file(path));
                return;
            }
        }
        ServletUtil.write(response, Result.ok().toJSONString(), "text/plain");
    }

    @ApiOperation("保存打印记录")
    @PostMapping("/printRecord/save")
    public Result savePrintRecord(@RequestBody ModulePrintRecord printRecord) {
        modulePrintTemplateService.savePrintRecord(printRecord);
        return Result.ok();
    }

    @ApiOperation("查询打印记录列表")
    @PostMapping("/printRecord/list/{moduleId}")
    public Result<List<ModulePrintRecord>> queryPrintRecord(@PathVariable("moduleId") Long moduleId) {
        List<ModulePrintRecord> crmPrintRecords = modulePrintTemplateService.queryPrintRecord(moduleId);
        return Result.ok(crmPrintRecords);
    }

    @ApiOperation("查询打印记录详情")
    @PostMapping("/printRecord/{recordId}")
    public Result<ModulePrintRecord> queryPrintRecordById(@PathVariable("recordId") Long recordId) {
        ModulePrintRecord crmPrintRecord = modulePrintTemplateService.queryPrintRecordById(recordId);
        return Result.ok(crmPrintRecord);
    }



}
