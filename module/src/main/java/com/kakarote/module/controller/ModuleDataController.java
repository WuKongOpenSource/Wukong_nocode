package com.kakarote.module.controller;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.Result;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.constant.FieldSearchEnum;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.service.IModuleDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/moduleData")
@Api(tags = "模块数据操作")
public class ModuleDataController {

    @Autowired
    private IModuleDataService iModuleDataService;

    @PostMapping("/queryPageList/{moduleId}")
    @ApiOperation("查询列表页数据")
    public Result<BasePage<Map<String, Object>>> queryPageList(@PathVariable("moduleId") Long moduleId, @RequestBody SearchBO search) {
        search.setPageType(1);
        BasePage<Map<String, Object>> mapBasePage = iModuleDataService.queryList(search, moduleId);
        return Result.ok(mapBasePage);
    }

    @PostMapping("/queryByDataIds")
    @ApiOperation("查询指定数据列表数据")
    public Result<BasePage<Map<String, Object>>> queryByDataIds(@RequestBody ModuleDataQueryBO queryBO) {
        BasePage<Map<String, Object>> mapBasePage = iModuleDataService.queryByDataIds(queryBO);
        return Result.ok(mapBasePage);
    }

    @PostMapping("/allExportExcel")
    @ApiOperation("全部导出")
    public void allExportExcel(@RequestBody ModuleExportBO exportBO, HttpServletResponse response) {
        exportBO.getSearch().setPageType(0);
        exportBO.getSearch().setModuleId(exportBO.getModuleId());

        FieldQueryBO queryBO = new FieldQueryBO();
        queryBO.setCategoryId(exportBO.getCategoryId());
        queryBO.setVersion(exportBO.getVersion());
        queryBO.setModuleId(exportBO.getModuleId());
        iModuleDataService.exportExcel(response, queryBO, exportBO.getSearch(), exportBO.getSortIds(), exportBO.getIsXls());
    }

    @PostMapping("/batchExportExcel")
    @ApiOperation("选中导出")
    public void batchExportExcel(@RequestBody ModuleExportBO exportBO, HttpServletResponse response) {
        SearchBO search = new SearchBO();
        search.setPageType(0);
        search.setModuleId(exportBO.getModuleId());
        SearchEntityBO entity = new SearchEntityBO();
        entity.setSearchEnum(FieldSearchEnum.ID);
        entity.setType(11);
        entity.setFormType(ModuleFieldEnum.TEXT.getFormType());
        entity.setValues(exportBO.getIds().stream().map(Object::toString).collect(Collectors.toList()));
        search.getSearchList().add(entity);
        search.setPageType(0);

        FieldQueryBO queryBO = new FieldQueryBO();
        queryBO.setCategoryId(exportBO.getCategoryId());
        queryBO.setVersion(exportBO.getVersion());
        queryBO.setModuleId(exportBO.getModuleId());
        iModuleDataService.exportExcel(response, queryBO, search, exportBO.getSortIds(), exportBO.getIsXls());
    }

    @PostMapping("/downloadExcel")
    @ApiOperation("下载导入模板")
    public void downloadExcel(@RequestBody FieldQueryBO queryBO, HttpServletResponse response) {
        iModuleDataService.downloadExcel(queryBO, response);
    }

    @PostMapping("/uploadExcel")
    @ApiOperation("导入对应模块数据")
    public Result<Long> uploadExcel(@RequestPart("file") MultipartFile file, @RequestPart("data") UploadExcelBO uploadExcelBO) {
        uploadExcelBO.setUserInfo(UserUtil.getUser());
        Long messageId = iModuleDataService.uploadExcel(file, uploadExcelBO);
        return Result.ok(messageId);
    }
}
