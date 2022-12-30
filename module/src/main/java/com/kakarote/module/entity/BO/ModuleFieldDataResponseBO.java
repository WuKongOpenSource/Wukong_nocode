package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.FlowExamineRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-10 13:48
 */
@Data
@ToString
@ApiModel(value = "ModuleFieldDataResponseBO 对象", description = "ModuleFieldDataResponseBO")
public class ModuleFieldDataResponseBO extends ModuleFieldDataSaveBO {

	@ApiModelProperty(value = "通用模块字段值表")
	private ModuleFieldDataCommonBO fieldDataCommon;

	@ApiModelProperty(value = "审核记录")
	private FlowExamineRecord examineRecord;
}
