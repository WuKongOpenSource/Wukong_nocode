package com.kakarote.module.entity.BO;

import com.kakarote.module.constant.FieldSearchEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("流程数据筛选规则BO")
public class FlowConditionBO {

	@ApiModelProperty(value = "筛选类型：1 条件筛选 2 更新节点筛选 3 更新节点更新 4 更新节点添加 5 添加节点添加")
	private Integer ruleType;

	@ApiModelProperty(value = "模式：0 简单, 1 高级")
	private Integer model;

	@ApiModelProperty(value = "类型：0 自定义,1 匹配字段")
	private Integer type;

	@ApiModelProperty(value = "筛选/更新规则")
	private CommonConditionBO search;

	@ApiModelProperty(value = "分组ID")
	private Integer groupId;

	@ApiModelProperty(value = "字段ID")
	private Long fieldId;

	@ApiModelProperty(value = "表格字段更新规则")
	private List<FlowTableConditionDataBO> tableConditions;


	public static class FlowTableConditionDataBO {
		@ApiModelProperty(value = "目标表格字段ID")
		private Long targetFieldId;

		@ApiModelProperty(value = "类型：0 自定义,1 匹配字段")
		private Integer type;

		@ApiModelProperty(value = "连接条件")
		private FieldSearchEnum conditionType;

		@ApiModelProperty(value = "值列表")
		private List<String> values = new ArrayList<>();

		@ApiModelProperty(value = "当前表格ID")
		private Long currentFieldId;
	}
}
