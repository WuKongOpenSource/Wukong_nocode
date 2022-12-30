package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-08 15:05
 */
@Data
@ApiModel("数据关联筛选条件BO")
public class ModuleFieldUnionConditionBO {

	@ApiModelProperty(value = "模式：0 简单, 1 高级")
	private Integer model;

	@ApiModelProperty(value = "类型：0 自定义,1 匹配字段")
	private Integer type;

	@ApiModelProperty(value = "筛选条件")
	private SearchEntityBO search;

	@NotNull
	@ApiModelProperty(value = "分组ID")
	private Integer groupId;
}
