package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
@ApiModel("字段验证对象")
public class ModuleFieldVerifyBO {

    @NotNull
    @ApiModelProperty("模块ID")
    private Long moduleId;

    @NotNull
    @ApiModelProperty("字段ID")
    private Long fieldId;

    @NotNull
    @ApiModelProperty("版本号")
    private Integer version;

    @ApiModelProperty("值")
    private String value;

    @ApiModelProperty("dataId")
    private String dataId;

    @ApiModelProperty("状态")
    private Integer status = 0;

    @ApiModelProperty("负责人")
    private String ownerUserName;
}
