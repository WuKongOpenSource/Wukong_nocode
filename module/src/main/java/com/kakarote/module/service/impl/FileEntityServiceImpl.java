package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.common.upload.service.FileService;
import com.kakarote.common.utils.BaseUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.constant.UploadTypeEnum;
import com.kakarote.module.entity.BO.FileDeleteRequestBO;
import com.kakarote.module.entity.BO.FileRenameRequestBO;
import com.kakarote.module.entity.PO.FileEntity;
import com.kakarote.module.entity.VO.FileEntityVO;
import com.kakarote.module.entity.VO.FileUploadResultVO;
import com.kakarote.module.mapper.FileEntityMapper;
import com.kakarote.module.service.IFileEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : zjj
 * @since : 2023/1/9
 */
@Service
public class FileEntityServiceImpl extends BaseServiceImpl<FileEntityMapper, FileEntity> implements IFileEntityService {

    @Value("${wukong.common.upload.domain}")
    private String domain;

    @Value("${wukong.common.upload.type}")
    private String uploadType;

    @Autowired
    private FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResultVO upload(MultipartFile file,
                                     String batchId,
                                     Boolean overwrite,
                                     String type,
                                     Boolean isPublic) throws IOException {
        if (StrUtil.isEmpty(batchId)) {
            batchId = IdUtil.simpleUUID();
        }
        Long fileId = BaseUtil.getNextId();
        FileUploadResultVO uploadEntity = new FileUploadResultVO();
        uploadEntity.setFileId(fileId.toString());
        uploadEntity.setBatchId(batchId);
        uploadEntity.setUrl(getUrl(fileId));
        uploadEntity.setName(file.getOriginalFilename());
        uploadEntity.setSize(file.getSize());
        fileService.uploadFile(file.getInputStream(), uploadEntity);
        if (overwrite) {
            lambdaUpdate().eq(FileEntity::getBatchId, batchId).remove();
        }
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileId(fileId);
        fileEntity.setName(uploadEntity.getName());
        fileEntity.setSize(uploadEntity.getSize());
        fileEntity.setPath(uploadEntity.getPath());
        fileEntity.setBatchId(batchId);
        if (StrUtil.isEmpty(type)) {
            type = "file";
        }
        fileEntity.setFileType(type);
        fileEntity.setType(UploadTypeEnum.parse(uploadType).getConfig());
        fileEntity.setIsPublic(isPublic);
        fileEntity.setCreateTime(LocalDateTime.now());
        fileEntity.setCreateUserId(UserUtil.getUserId());
        save(fileEntity);
        return uploadEntity;
    }

    @Override
    public void download(Long fileId, HttpServletResponse response) {
        FileEntity fileEntity = getById(fileId);
        if (ObjectUtil.isNotNull(fileEntity)) {
            UploadEntity uploadEntity = new UploadEntity();
            uploadEntity.setPath(fileEntity.getPath());
            InputStream inputStream = fileService.downFile(uploadEntity);
            String contentType = ObjectUtil.defaultIfNull(FileUtil.getMimeType(fileEntity.getName()), "application/octet-stream");
            ServletUtil.write(response, inputStream, contentType, fileEntity.getName());
        }
    }

    @Override
    public void deleteById(Long fileId) {
        FileEntity fileEntity = getById(fileId);
        if (ObjectUtil.isNotNull(fileEntity)) {
            fileService.deleteFile(fileEntity.getPath());
            removeById(fileId);
        }
    }

    @Override
    public void deleteByBatchId(FileDeleteRequestBO requestBO) {
        Integer type = requestBO.getType();
        // 1 附件 2 图片
        String fileType = "file";
        if (Objects.equals(2, type)) {
            fileType = "img";
        }
        List<FileEntity> fileList = lambdaQuery()
                .select(FileEntity::getFileId)
                .eq(FileEntity::getBatchId, requestBO.getBatchId())
                .eq(StrUtil.isNotEmpty(fileType), FileEntity::getFileType, fileType)
                .list();
        if (CollUtil.isEmpty(fileList)) {
            return;
        }
        List<String> pathList = fileList.stream().map(FileEntity::getPath).collect(Collectors.toList());
        fileService.deleteFileBatch(pathList);
        List<Long> fileIdList = fileList.stream().map(FileEntity::getFileId).collect(Collectors.toList());
        removeByIds(fileIdList);
    }

    @Override
    public void renameFile(FileRenameRequestBO requestBO) {
        lambdaUpdate()
                .set(FileEntity::getName, requestBO.getName())
                .eq(FileEntity::getFileId, requestBO.getFileId())
                .update();
    }

    @Override
    public List<FileEntityVO> queryFileList(String batchId) {
        if (StrUtil.isEmpty(batchId)) {
            return new ArrayList<>();
        }
        List<FileEntity> fileEntities = lambdaQuery().eq(FileEntity::getBatchId, batchId).list();
        return fileEntities.stream().map(this::transVO).collect(Collectors.toList());
    }

    @Override
    public FileEntityVO queryByBatchId(String batchId) {
        FileEntity fileEntity = lambdaQuery().eq(FileEntity::getBatchId, batchId).one();
        if (ObjectUtil.isNull(fileEntity)) {
            return null;
        }
        return transVO(fileEntity);
    }

    private FileEntityVO transVO(FileEntity fileEntity) {
        FileEntityVO vo = new FileEntityVO();
        vo.setIsPublic(fileEntity.getIsPublic().toString());
        vo.setPath(fileEntity.getPath());
        vo.setUrl(this.getUrl(fileEntity.getFileId()));
        vo.setFileId(fileEntity.getFileId());
        vo.setName(fileEntity.getName());
        vo.setCreateUserName(UserCacheUtil.getUserName(fileEntity.getCreateUserId()));
        vo.setSize(fileEntity.getSize());
        vo.setBatchId(fileEntity.getBatchId());
        vo.setFileType(fileEntity.getFileType());
        return vo;
    }


    @Override
    public String getUrl(Long fieldId) {
        return StrUtil.join("/", domain, fieldId);
    }
}
