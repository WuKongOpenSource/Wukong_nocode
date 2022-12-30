package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-08 14:45
 */
@Data
@ApiModel("数据关联保存BO")
public class ModuleFieldUnionSaveBO {

	@ApiModelProperty("模块ID")
	private Long moduleId;

	@ApiModelProperty("数据关联字段ID")
	private Long relateFieldId;

	@NotNull
	@ApiModelProperty("数据关联字段临时ID")
	private String TempRelateFieldId;

	@ApiModelProperty(value = "目标模块ID")
	private Long targetModuleId;

	@ApiModelProperty(value = "目标分类ID")
	private String targetCategoryIds;

	@ApiModelProperty("关联字段列表")
	List<ModuleFieldUnionBO> fieldUnionList = new ArrayList<>();

	@ApiModelProperty("数据关联字段筛选条件")
	List<ModuleFieldUnionConditionBO> fieldUnionConditionList = new ArrayList<>();


}
