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
public class FileDeleteRequestBO {

    private String batchId;

    @ApiModelProperty("1 附件 2 图片")
    private Integer type;
}
