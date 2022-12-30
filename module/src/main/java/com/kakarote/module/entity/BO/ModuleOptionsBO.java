package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("字段选项BO")
public class ModuleOptionsBO {

    @NotNull
    @ApiModelProperty(value = "选项ID")
    private String key;

    @ApiModelProperty(value = "选项值")
    private String value;

	@ApiModelProperty(value = "选项类型：0 普通 1 其他")
	private Integer type;
}
