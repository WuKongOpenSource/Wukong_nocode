package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @description: AppStoreBO
 * @date 2022/6/11
 */
@Data
@ApiModel(value = "应用收藏 BO", description = "应用收藏 BO")
public class AppStoreBO {

    @ApiModelProperty(value = "应用ID")
    private String applicationId;

    @ApiModelProperty(value = "取消收藏")
    private Boolean isCancel = false;

}
