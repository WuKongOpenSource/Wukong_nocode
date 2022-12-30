package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : zjj
 * @since : 2022/12/3
 */
@Data
@ApiModel(value = "树数据展示查询 BO", description = "树数据展示查询 BO")
public class TreeDataQueryBO {

    @ApiModelProperty("模块 ID")
    private Long moduleId;

    @ApiModelProperty("字段 ID")
    private Long fieldId;

    @ApiModelProperty("数据 ID")
    private Long dataId;
}
