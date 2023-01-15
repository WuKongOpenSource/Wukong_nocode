package com.kakarote.module.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : zjj
 * @since : 2023/1/9
 */
@Data
public class FileEntityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文件ID")
    private Long fileId;

    @ApiModelProperty("文件类型")
    private String fileType;

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("文件大小")
    private Long size;

    @ApiModelProperty("批次ID")
    private String batchId;

    @ApiModelProperty("url")
    private String url;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    private Long createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    private String isPublic;

    private String path;
}
