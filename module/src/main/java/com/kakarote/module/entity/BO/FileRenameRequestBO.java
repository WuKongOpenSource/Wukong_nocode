package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : zjj
 * @since : 2023/1/9
 */
@Getter
@Setter
public class FileRenameRequestBO {

    @ApiModelProperty(value = "文件 id")
    private Long fileId;

    @ApiModelProperty(value = "名称")
    private String name;
}
