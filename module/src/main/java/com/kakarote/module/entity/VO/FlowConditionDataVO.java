package com.kakarote.module.entity.VO;

import com.kakarote.module.constant.FieldSearchEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-28 17:56
 */
@Data
@ApiModel("条件节点条件数据VO")
public class FlowConditionDataVO {

	@ApiModelProperty(value = "字段中文名")
	private String name;

	@ApiModelProperty(value = "字段名称")
	private String fieldName;

	@ApiModelProperty(value = "格式")
	private String formType;

	@ApiModelProperty(value = "连接条件")
	private FieldSearchEnum conditionType;
}
