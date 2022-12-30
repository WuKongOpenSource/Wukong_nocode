package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zjj
 * @date: 2021-06-05 09:54
 */
@Data
@ApiModel("查询流程条件 BO")
public class FlowConditionQueryBO {

	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

	@ApiModelProperty(value = "版本号")
	private Integer version;

	@ApiModelProperty(value = "流程Id")
	private Long metadataId;

	@ApiModelProperty(value = "审核记录id")
	private Long recordId;
}
