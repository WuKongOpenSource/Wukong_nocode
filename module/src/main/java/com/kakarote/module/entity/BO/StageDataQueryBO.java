package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: StageDataQueryBO
 * @description: 阶段数据查询BO
 * @date 2022/4/12 17:28
 */
@Data
@ApiModel(value = "阶段数据查询BO", description = "阶段数据查询BO")
public class StageDataQueryBO {

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "数据ID")
    private Long dataId;
}
