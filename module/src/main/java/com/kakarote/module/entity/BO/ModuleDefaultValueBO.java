package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("字段默认值BO")
public class ModuleDefaultValueBO {

	@NotNull
	@ApiModelProperty("模块ID")
	private Long moduleId;

	@NotNull
	@ApiModelProperty(value = "本地临时字段ID")
	private String tempFieldId;

	@ApiModelProperty(value = "当前字段ID")
	private Long fieldId;

	@ApiModelProperty(value = "选项ID")
	private Object key;

	@ApiModelProperty(value = "默认值")
	private Object value;

	@ApiModelProperty(value = "默认值类型 1 固定值 2 自定义筛选 3 公式")
	private Integer type;

	@ApiModelProperty(value = "公式")
	private String formula;

	@ApiModelProperty(value = "目标模块ID")
	private Long targetModuleId;

	@ApiModelProperty(value = "目标字段ID")
	private Long targetFieldId;

	@ApiModelProperty("数据关联字段筛选条件")
	List<ModuleFieldUnionConditionBO> fieldUnionConditionList = new ArrayList<>();

	@ApiModelProperty("表格默认值填充配置")
	List<ModuleFieldUnionBO> fieldUnionList = new ArrayList<>();

}
