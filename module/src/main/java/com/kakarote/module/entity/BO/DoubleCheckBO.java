package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author zjj
 * @title: DoubleCheckBO
 * @description: 验重
 * @date 2021/12/1311:21
 */
@Data
@ToString
@ApiModel(value = "验重", description = "验重")
public class DoubleCheckBO {

    @ApiModelProperty(value = "模块ID")
    @NotNull
    private Long moduleId;

    @ApiModelProperty(value = "数据ID")
    private Long dataId;

    @ApiModelProperty(value = "字段ID")
    @NotNull
    private Long fieldId;

    @ApiModelProperty(value = "字段值")
    @NotNull
    private String value;
}
