package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-10 11:20
 */
@Data
@ToString
@ApiModel(value = "ModuleFieldDataDeleteBO 模块字段值删除", description = "模块字段值删除")
public class ModuleFieldDataDeleteBO {

	@ApiModelProperty(value = "模块ID")
	@NotNull
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

	@ApiModelProperty(value = "待删除的数据ID")
	private List<Long> dataIds;
}
