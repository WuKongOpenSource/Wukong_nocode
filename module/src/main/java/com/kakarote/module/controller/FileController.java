package com.kakarote.module.controller;

import com.kakarote.common.result.Result;
import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.module.entity.BO.FileDeleteRequestBO;
import com.kakarote.module.entity.BO.FileRenameRequestBO;
import com.kakarote.module.entity.VO.FileEntityVO;
import com.kakarote.module.service.IFileEntityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

/**
 * @author : zjj
 * @since : 2023/1/9
 */
@RestController
@RequestMapping("/commonFile")
@Api(tags = "文件")
public class FileController {

    @Autowired
    private IFileEntityService fileEntityService;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<UploadEntity> upload(@RequestParam("file") @ApiParam("文件") MultipartFile file,
                                       @ApiParam("batchId") String batchId,
                                       @RequestParam(value = "overwrite", required = false) boolean overwrite,
                                       @ApiParam("文件类型") String type,
                                       @RequestParam(value = "isPublic", required = false) Boolean isPublic) throws IOException {
        UploadEntity uploadEntity = fileEntityService.upload(file, batchId, overwrite, type, isPublic);
        return Result.ok(uploadEntity);
    }

    @GetMapping(value = "/download/{fileId}")
    @ApiOperation(value = "下载文件接口")
    public void down(@PathVariable("fileId") Long fileId, HttpServletResponse response) {
       fileEntityService.download(fileId, response);
    }

    @ApiOperation("删除文件")
    @PostMapping("/deleteById/{fileId}")
    public Result deleteById(@NotNull @PathVariable @ApiParam("文件ID") Long fileId) {
        fileEntityService.deleteById(fileId);
        return Result.ok();
    }

    @ApiOperation("删除文件")
    @PostMapping("/deleteByBatchId")
    public Result deleteByBatchId(@RequestBody FileDeleteRequestBO requestBO) {
        fileEntityService.deleteByBatchId(requestBO);
        return Result.ok();
    }

    @ApiOperation("修改文件名称")
    @PostMapping("/renameFile")
    public Result renameFile(@RequestBody FileRenameRequestBO requestBO) {
        fileEntityService.renameFile(requestBO);
        return Result.ok();
    }

    @ApiOperation("查询文件列表")
    @PostMapping("/fileList/{batchId}")
    public Result<List<FileEntityVO>> fileList(@PathVariable @ApiParam("batchId") String batchId) {
        List<FileEntityVO> result = fileEntityService.queryFileList(batchId);
        return Result.ok(result);
    }

    @ApiOperation("查询文件")
    @PostMapping("/file/{batchId}")
    public Result<FileEntityVO> queryByBatchId(@NotNull @PathVariable @ApiParam("batchId") String batchId) {
        FileEntityVO result = fileEntityService.queryByBatchId(batchId);
        return Result.ok(result);
    }
}
