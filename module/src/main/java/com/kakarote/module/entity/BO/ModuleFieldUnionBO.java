package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:数据关联BO
 * @author: zjj
 * @date: 2021-05-08 15:02
 */
@Data
@ApiModel("数据关联BO")
public class ModuleFieldUnionBO {

	@ApiModelProperty(value = "当前字段ID")
	private Long fieldId;

	@NotNull
	@ApiModelProperty(value = "当前字段临时ID")
	private String tempFieldId;

	@ApiModelProperty(value = "目标字段ID")
	private Long targetFieldId;

	@ApiModelProperty(value = "目标字段名")
	private String targetFieldName;

	@ApiModelProperty(value = "字段类型")
	private Integer targetFieldType;

}
