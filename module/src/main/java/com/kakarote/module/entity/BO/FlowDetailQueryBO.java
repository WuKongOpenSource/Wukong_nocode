package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "节点详情查询BO", description = "节点详情查询BO")
public class FlowDetailQueryBO {
    @ApiModelProperty(value = "模块 ID")
    @NotNull
    private Long moduleId;

    @ApiModelProperty(value = "数据 ID")
    @NotNull
    private Long dataId;

    @ApiModelProperty(value = "类型 ID")
    private Long typeId;
}
