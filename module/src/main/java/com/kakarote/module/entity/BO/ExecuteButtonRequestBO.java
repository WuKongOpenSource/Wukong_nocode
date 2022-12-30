package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: ExecuteButtonRequestBO
 * @description: 执行按钮请求BO
 * @date 2022/3/19 14:45
 */
@Data
@ApiModel("执行按钮请求 BO")
public class ExecuteButtonRequestBO {

    @ApiModelProperty(value = "当前模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "按钮ID")
    private Long buttonId;

    @ApiModelProperty(value = "数据ID")
    private Long dataId;
}
