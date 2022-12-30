package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zjj
 * @title: ModuleFieldStyleSaveBO
 * @description: setFieldStyle
 * @date 2021/11/2215:38
 */
@Data
@ApiModel(value = "setFieldStyle", description = "setFieldStyle")
public class ModuleFieldStyleSaveBO {

    @ApiModelProperty(value = "字段ID")
    @NotNull
    private Long fieldId;

    @ApiModelProperty(value = "模块ID")
    @NotNull
    private Long moduleId;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    @ApiModelProperty(value = "字段宽度")
    private Integer style;

    @ApiModelProperty(value = "字段锁定")
    private Boolean isLock;
}
