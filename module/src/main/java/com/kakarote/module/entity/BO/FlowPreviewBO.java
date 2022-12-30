package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-31 09:37
 */
@Data
@ApiModel("预览流程BO")
public class FlowPreviewBO {

	@NotNull
	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

	@NotNull
	@ApiModelProperty(value = "版本号")
	private Integer version;

	@ApiModelProperty(value = "搜索条件中字段值列表")
	private List<ModuleFieldValueBO> fieldValues;

	@ApiModelProperty(value = "审核记录ID")
	private Long recordId;

    @ApiModelProperty(value = "流程类型 0 系统 1 自定义按钮")
    private Integer flowMetadataType;

    @ApiModelProperty(value = "对应类型ID")
    private Long typeId;
}
