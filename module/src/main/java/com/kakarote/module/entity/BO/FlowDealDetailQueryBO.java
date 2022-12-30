package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author zjj
 * @title: FlowDealDetailQueryBO
 * @description: 节点处理详情查询BO
 * @date 2021/12/1114:17
 */
@Data
@ToString
@ApiModel(value = "节点处理详情查询BO", description = "节点处理详情查询BO")
public class FlowDealDetailQueryBO {

    @ApiModelProperty(value = "模块ID")
    @NotNull
    private Long moduleId;

    @NotNull
    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "数据ID")
    @NotNull
    private Long dataId;

    @ApiModelProperty(value = "流程ID")
    @NotNull
    private Long flowId;
}
