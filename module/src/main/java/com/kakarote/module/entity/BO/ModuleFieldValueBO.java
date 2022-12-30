package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:字段值BO
 * @author: zjj
 * @date: 2021-05-21 13:47
 */
@Data
@ApiModel("字段值BO")
public class ModuleFieldValueBO {
	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

	@ApiModelProperty(value = "字段Id")
	private Long fieldId;

	@ApiModelProperty(value = "字段类型")
	private Integer type;

	@ApiModelProperty(value = "类型")
	private String formType;

	@ApiModelProperty(value = "0 系统 1 自定义")
	private Integer fieldType;

	@ApiModelProperty(value = "字段名称")
	private String fieldName;

    @ApiModelProperty(value = "字段名称")
    private String name;

	@ApiModelProperty(value = "字段值")
	private String value;
}
